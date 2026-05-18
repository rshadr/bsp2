####
#### Copyright 2026 rshadr (rshadr@assembly-cave.tw)
#### See LICENSE for details
####
all: build/testprog

CC=gcc -std=c23
CFLAGS=-Wall -Wextra -pedantic -O2 -ggdb3
LFLAGS=-lm

build/testprog: src/testprog.c
	@mkdir -p $(@D)
	$(CC) -o $@ $(CFLAGS) $< $(LDFLAGS)

clean:
	rm -rf build

.PHONY: all clean

