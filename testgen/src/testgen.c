/*
* Copyright 2026 rshadr (rshadr@assembly-cave.tw)
* See LICENSE for details
*/
#include <inttypes.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>


constexpr int32_t MAX_TASKS = 100;
#define LEN(a) \
  (sizeof((a)) / sizeof((a)[0]))

static inline int32_t
abs_i32 (int32_t x)
{
  return x < 0 ? -x : +x;
}


typedef struct TgConfig_s {
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
    int32_t flat[8];
  };
} TgConfig;

typedef struct TgTask_s {
  int32_t priority;
  int32_t initial_start_time;
  int32_t wcet;
  int32_t rel_deadline;
  int32_t min_period;
} TgTask;


typedef struct TgState_s {
  TgConfig const *cfg;
  TgTask tasks[MAX_TASKS];
} TgState;


typedef enum TgCmdArgType_e {
  TG_CMDARG_MAX_DURATION       = 0,
  TG_CMDARG_NUM_PROCESSORS     = 1,
  TG_CMDARG_MAX_SPORADIC_DELAY = 2,
  TG_CMDARG_NUM_TASKS          = 3,
  TG_CMDARG_MAX_INITIAL_TIME   = 4,
  TG_CMDARG_MAX_WCET           = 5,
  TG_CMDARG_MAX_MIN_PERIOD     = 6,
  TG_CMDARG_RANDOM_SEED        = 7,
  TG_CMDARG_INVALID            = -1,
} TgCmdArgType;


static char const *k_tg_cmd_arg_keys[] = {
  [TG_CMDARG_MAX_DURATION]       = "max-duration",
  [TG_CMDARG_NUM_PROCESSORS]     = "num-processors",
  [TG_CMDARG_MAX_SPORADIC_DELAY] = "max-sporadic-delay",
  [TG_CMDARG_NUM_TASKS]          = "num-tasks",
  [TG_CMDARG_MAX_INITIAL_TIME]   = "max-initial-time",
  [TG_CMDARG_MAX_WCET]           = "max-wcet",
  [TG_CMDARG_MAX_MIN_PERIOD]     = "max-min-period",
  [TG_CMDARG_RANDOM_SEED]        = "random-seed",
};


[[noreturn]]
static void
die (char const *fmt, ...)
{
  va_list ap;

  va_start(ap, fmt);
  vfprintf(stderr, fmt, ap);
  va_end(ap);

  exit(EXIT_FAILURE);
}


static inline int32_t
min (int32_t a, int32_t b)
{
  return a < b ? a : b;
}


static inline int32_t
max (int32_t a, int32_t b)
{
  return a > b ? a : b;
}


static TgCmdArgType
tg_getCmdOptKey (char const *arg,
               char const **ret_value)
{
  assert( arg != nullptr );
  assert( ret_value != nullptr );

  if (strncmp(arg, "--", 2) != 0)
    return TG_CMDARG_INVALID;

  char const *key = &arg[2];
  size_t key_len = 0;
  for (; key[key_len] != '='; ++key_len)
    if (key[key_len] == '\0')
      return TG_CMDARG_INVALID;

  char const *value = &key[key_len + 1]; /* skip '=' */

  for (size_t i = 0; i < LEN(k_tg_cmd_arg_keys); ++i) {
    if (strlen(k_tg_cmd_arg_keys[i]) != key_len)
      continue;

    if (!strncmp(k_tg_cmd_arg_keys[i], key, key_len)) {
      *ret_value = value;
      return (TgCmdArgType)(i);
    }
  }

  return TG_CMDARG_INVALID;
}


static void
tgConfig_fromCmdArgs (size_t num_args, char const *args[num_args],
                    TgConfig * restrict cfg)
{
  for (size_t i = 0; i < LEN(cfg->flat); ++i)
    cfg->flat[i] = -1;

  for (size_t i = 0; i < num_args; ++i) {
    //printf("cmdarg[%zu]: '%s'\n", i, argv[i]);
    char const *value;
    auto type = tg_getCmdOptKey(args[i], &value);

    if (type == TG_CMDARG_INVALID)
      die("invalid option name: '%s'\n", args[i]);

    if (cfg->flat[type] != -1)
      die("duplicate option: '%s'\n", k_tg_cmd_arg_keys[type]);

    switch (type) {
      case TG_CMDARG_MAX_DURATION: {
        cfg->max_duration = atoi(value);
        assert( cfg->max_duration > 0 );
        break;
      }

      case TG_CMDARG_NUM_PROCESSORS: {
        cfg->num_processors = atoi(value);
        assert( cfg->num_processors > 0 );
        break;
      }

      case TG_CMDARG_MAX_SPORADIC_DELAY: {
        cfg->max_sporadic_delay = atoi(value);
        assert( cfg->max_sporadic_delay > 0 );
        break;
      }

      case TG_CMDARG_NUM_TASKS: {
        cfg->num_tasks = atoi(value);
        assert( cfg->num_tasks <= MAX_TASKS );
        break;
      }

      case TG_CMDARG_MAX_INITIAL_TIME: {
        cfg->max_initial_time = atoi(value);
        assert( cfg->max_initial_time >= 0 );
        break;
      }

      case TG_CMDARG_MAX_WCET: {
        cfg->max_wcet = atoi(value);
        assert( cfg->max_wcet > 0 );
        break;
      }

      case TG_CMDARG_MAX_MIN_PERIOD: {
        cfg->max_min_period = atoi(value);
        assert( cfg->max_min_period > 0 );
        break;
      }

      case TG_CMDARG_RANDOM_SEED: {
        cfg->random_seed = atoi(value);
        assert( cfg->random_seed >= 1);
        break;
      }

      default:
        //__builtin_unreachable();
        __builtin_trap();
    }
  }

  bool missing_options = false;
  for (size_t i = 0; i < LEN(cfg->flat); ++i) {
    if (cfg->flat[i] == -1) {
      missing_options = true;
      fprintf(stderr, "missing option: '%s'\n",
       k_tg_cmd_arg_keys[i]);
    }
  }
  if (missing_options)
    exit(EXIT_FAILURE);

  assert( cfg->max_initial_time < cfg->max_duration );
  assert( cfg->max_sporadic_delay < cfg->max_duration );
  assert( cfg->max_initial_time + cfg->max_sporadic_delay < cfg->max_duration );
  assert( cfg->max_sporadic_delay < cfg->max_min_period );
  assert( cfg->max_wcet < cfg->max_min_period );
  /* XXX: keep checking */
}



[[nodiscard]]
static TgState *
tgState_new (void)
{
  TgState *tgs = nullptr;
  tgs = calloc(1, sizeof(*tgs));

  for (size_t i = 0; i < MAX_TASKS; ++i) {
    tgs->tasks[i].priority = -1;
  }

  return tgs;
}


static void
tgState_destroy (TgState *tgs)
{
  assert( tgs != nullptr );

  free(tgs);
}


static int32_t
rand_range (const int32_t lo, volatile const int32_t hi)
{
  //printf("lo: %"PRIi32"; hi: %"PRIi32"\n", lo, hi);
  assert( lo < hi );
  int32_t v = abs_i32((int32_t)(rand()));

  const int32_t diff = hi - lo;
  v %= diff;
  v += lo;

  return v;
}


static void
tgState_generate (TgState *tgs, TgConfig const *cfg)
{
  assert( tgs != nullptr );
  assert( cfg != nullptr );

  tgs->cfg = cfg;

  srand(cfg->random_seed);

  for (int i = 0; i < cfg->num_tasks; ++i) {
    TgTask task = { .priority = i };

    //printf("max init time: %"PRIi32"\n", cfg->max_initial_time);
    task.initial_start_time = rand_range(0, cfg->max_initial_time + 1);
    task.min_period = rand_range(1, cfg->max_min_period + 1);
    task.rel_deadline = rand_range(1, task.min_period + 1);
    task.wcet = rand_range(1, max(
     min(cfg->max_wcet + 1, task.rel_deadline), 2));

    tgs->tasks[i] = task;
  }
}


static void
tgState_outputDistribution (TgState *tgs, FILE *outfile)
{
  (void) tgs;
  fprintf(outfile,
"  \"distribution\": {\n"
"    \"name\": \"Geometric\",\n"
"    \"options\": {\n"
"      \"p\": 0.67\n"
"    }\n"
"  },\n"
"\n"
  );
}


static void
tgState_outputTrackers (TgState *tgs, FILE *outfile)
{
  (void) tgs;

  static char const *debug_trackers =
"    {\n"
"      \"name\": \"History\",\n"
"      \"options\": {\n"
"        \"coerceSameTickDecisions\": true\n"
"      }\n"
"    },\n"
"\n"
"    {\n"
"      \"name\": \"Delay\",\n"
"      \"options\": {}\n"
"    },\n";

  fprintf(outfile,
"  \"trackers\": [\n"
"%s\n"
"    {\n"
"      \"name\": \"SlackTime\",\n"
"      \"options\": {}\n"
"    }\n"
"  ],\n"
"\n",
  debug_trackers);
}


static void
tgState_output (TgState *tgs, FILE *outfile)
{
  auto cfg = tgs->cfg;

  fprintf(outfile,
"{\n"
  );

  fprintf(outfile,
"  \"maxDuration\": %"PRIi32",\n"
"  \"numProcessors\": %"PRIi32",\n"
"  \"maxSporadicDelay\": %"PRIi32",\n"
"\n",
  cfg->max_duration, cfg->num_processors, cfg->max_sporadic_delay
  );

  tgState_outputDistribution(tgs, outfile);

  tgState_outputTrackers(tgs, outfile);

fprintf(outfile,
"  \"tasks\": [\n"
);
  for (int i = 0; i < cfg->num_tasks; ++i) {
    TgTask const *task = &tgs->tasks[i];
    if (task->priority == -1)
      continue;

    fprintf(outfile,
"    {\n"
"      \"priority\": %"PRIi32",\n"
"      \"initialStartTime\": %"PRIi32",\n"
"      \"wcet\": %"PRIi32",\n"
"      \"relativeDeadline\": %"PRIi32",\n"
"      \"minIAT\": %"PRIi32"\n"
"    }%s\n",
      task->priority,
      task->initial_start_time,
      task->wcet,
      task->rel_deadline,
      task->min_period,
      i < cfg->num_tasks - 1 ? "," : ""
    );
  }
fprintf(outfile,
"  ]\n"
);

  fprintf(outfile, "}\n");
}


int
main (int argc, char const *argv[argc])
{
  for (int i = 1; i < argc; ++i)
    if (!strcmp("-h", argv[i]))
      die("usage: %s <<outfile> <options> | -h>\n", argv[0]);

  if (argc < 2)
    die("missing outfile\n");

  TgConfig cfg;
  tgConfig_fromCmdArgs((size_t)(argc - 2), &argv[2], &cfg);


  char const *outfile_name = argv[1];
  FILE *outfile = fopen(outfile_name, "w");
  if (!outfile)
    die("failed to open file '%s'\n", outfile_name);

  TgState *tgs = nullptr;
  tgs = tgState_new();

  tgState_generate(tgs, &cfg);
  tgState_output(tgs, outfile);

  tgState_destroy(tgs);
  fclose(outfile);

  return EXIT_SUCCESS;
}

