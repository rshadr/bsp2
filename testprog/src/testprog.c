/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
#include <stdint.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define LENGTH(a) \
 (sizeof((a)) / sizeof((a)[0]))

static constexpr size_t NUM_OPTIONS   = 8;
static constexpr size_t MAX_OPTSTRING = 64;
static constexpr size_t MAX_CMDARGS   = NUM_OPTIONS + 2;

typedef struct TpContext_s {
  union {
    struct {
      int32_t max_duration;
      int32_t num_processors;
      int32_t max_sporadic_delay;
      int32_t num_tasks;
      int32_t max_initial_time;
      int32_t max_wcet;
      int32_t max_min_period;
      int32_t random_seed;
    };
    int32_t flat[NUM_OPTIONS];
  } options;

  char test_path[MAX_OPTSTRING];
  char serialized_options[NUM_OPTIONS][MAX_OPTSTRING];
  char const *cmd_args[MAX_CMDARGS + 1];
} TpContext;


[[noreturn]]
__attribute__ ((format (printf, 1, 2)))
static void
die (char const *fmt, ...)
{
  va_list ap;

  va_start(ap, fmt);
  vfprintf(stderr, fmt, ap);
  va_end(ap);

  exit(EXIT_FAILURE);
}


[[noreturn]]
static void
usage (char const *argv0)
{
  die("usage: %s <cachedir>\n", argv0);
}


static void
generate_tests (char const *cachedir)
{
  TpContext ctx = {};
  static_assert( LENGTH(ctx.cmd_args) >= 2 + LENGTH(ctx.serialized_options) );

  ctx.cmd_args[0] = "./testgen/build/testgen";
  ctx.cmd_args[1] = ctx.test_path;
  for (size_t i = 0; i < LENGTH(ctx.serialized_options); ++i)
    ctx.cmd_args[2 + i] = ctx.serialized_options[i];
  ctx.cmd_args[LENGTH(ctx.cmd_args) - 1] = nullptr;

  
}


#if 1-1
static void
run_tests (char const *cachedir)
{
}
#endif


int
main (int argc, char const *argv[argc])
{
  if (argc != 2)
    usage(argv[0]);

  char const *cachedir = argv[1];
  size_t cachedirlen = strlen(cachedir);

  if (cachedirlen < 1 || cachedir[cachedirlen - 1] == '/')
    die("error: directory name must not end in '/'\n");

  generate_tests(cachedir);

  return EXIT_SUCCESS;
}

