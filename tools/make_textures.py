#!/usr/bin/env python3
"""Draw the magnet item texture.

This script is the source of the sprite; the PNG under src/main/resources is its
output and is not edited by hand.

Shape is described once, as regions of a 16x16 grid. Shading is derived rather
than drawn: a pixel with nothing above-left of it catches the light, a pixel with
nothing below-right of it falls into shadow, and everything else is body colour.
So changing the shape does not mean redoing the shading, and two regions drawn
years apart still look like the same object.

No third-party libraries: the PNG is assembled from zlib and struct.

    python tools/make_textures.py
"""

from __future__ import annotations

import pathlib
import struct
import zlib

SIZE = 16

OUT = pathlib.Path(__file__).resolve().parents[1] / "src/main/resources/assets/magnes/textures/item/magnet.png"

# --- shape -----------------------------------------------------------------
# A horseshoe lying on its side, opening to the right: a spine down the left and
# two arms reaching off it.
#
# Not standing upright with the opening downward. Drawn that way the two arms read
# as legs, and with pale tips the whole thing turns into a pair of red trousers.
# Which way round a shape faces is not a detail at sixteen pixels.

SPINE = {y: range(3, 7) for y in range(2, 14)}
ARMS = {**{y: range(3, 14) for y in range(2, 6)},
        **{y: range(3, 14) for y in range(10, 14)}}


def cells(rows) -> set[tuple[int, int]]:
    return {(x, y) for y, xs in rows.items() for x in xs}


SHAPE = cells(SPINE) | cells(ARMS)

# The halves, split across the bend. Each arm carries its own pole all the way to
# the tip, which is how a magnet is drawn everywhere it is drawn: not a body with
# separate ends, but two coloured halves that meet.
NORTH = {(x, y) for (x, y) in SHAPE if y <= 7}
SOUTH = SHAPE - NORTH

# --- colour ----------------------------------------------------------------
# (light, body, shadow) per pole.

PALETTE = {
    "north": ("#E04A4A", "#C4212A", "#8A1219"),
    "south": ("#3A5FC8", "#1E3F9E", "#14276B"),
}


def _rgba(colour: str) -> tuple[int, int, int, int]:
    value = colour.lstrip("#")
    return (int(value[0:2], 16), int(value[2:4], 16), int(value[4:6], 16), 255)


def _tone(pixel: tuple[int, int], region: set[tuple[int, int]], tones: tuple[str, str, str]) -> str:
    """Light where the region ends going up-left, shadow where it ends down-right."""
    x, y = pixel
    light, body, shadow = tones
    if (x - 1, y - 1) not in region:
        return light
    if (x + 1, y + 1) not in region:
        return shadow
    return body


# --- output ----------------------------------------------------------------

def _png(pixels: dict[tuple[int, int], tuple[int, int, int, int]]) -> bytes:
    raw = bytearray()
    for y in range(SIZE):
        raw.append(0)  # filter type 0 for the row
        for x in range(SIZE):
            raw.extend(pixels.get((x, y), (0, 0, 0, 0)))

    def chunk(kind: bytes, data: bytes) -> bytes:
        body = kind + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body))

    header = struct.pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0)  # 8-bit RGBA
    return (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", header)
        + chunk(b"IDAT", zlib.compress(bytes(raw), 9))
        + chunk(b"IEND", b"")
    )


def draw() -> bytes:
    pixels: dict[tuple[int, int], tuple[int, int, int, int]] = {}
    for pixel in SHAPE:
        # Shaded against the whole silhouette, coloured by which pole it belongs to,
        # so the halves meet on a clean line instead of each casting an edge into the
        # other.
        tones = PALETTE["north"] if pixel in NORTH else PALETTE["south"]
        pixels[pixel] = _rgba(_tone(pixel, SHAPE, tones))
    return _png(pixels)


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_bytes(draw())
    print(f"wrote {OUT}")


if __name__ == "__main__":
    main()
