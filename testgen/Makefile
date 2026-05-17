####
#### Copyright 2026 rshadr (rshadr@assembly-cave.tw)
#### See LICENSE for details
####
all: build/testgen

CC=gcc -std=c23
CFLAGS=-Wall -Wextra -pedantic -O2 -ggdb3
LFLAGS=-lm

build/testgen: src/testgen.c
	@mkdir -p $(@D)
	$(CC) -o $@ $(CFLAGS) $< $(LDFLAGS)

clean:
	rm -rf build

.PHONY: all clean

