/*
 * Copyright 2026 rshadr
 * See LICENSE for details
 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

constexpr size_t MAX_TASKS = 100;


typedef struct TgConfig_s {
  size_t max_duration;
  // char const *distribution;
  size_t num_tasks;
} TgConfig;

typedef struct TgTask_s {
  size_t priority;
  size_t wcet;
  size_t initial_offset;
  size_t min_period;
  size_t rel_deadline;
} TgTask;


typedef struct TgState_s {
  TgConfig const *cfg;

  TgTask tasks[MAX_TASKS];
} TgState;


static void tgConfig_fromCmdArgs (size_t argc, char const *argv[argc],
                                  TgConfig * restrict cfg);
static TgState *tgState_new (void);
static void tgState_generate (TgState *tgs, TgConfig const *cfg);
static void tgState_output (TgState *tgs);
static void tgState_destroy (TgState *tgs);


static void
tgConfig_fromCmdArgs (size_t argc, char const *argv[argc],
                      TgConfig * restrict cfg)
{
  *cfg = (typeof(*cfg)){0};
  sscanf(argv[0], "%zu", &cfg->num_tasks);
}



[[nodiscard]]
static TgState *
tgState_new (void)
{
  TgState *tgs = nullptr;
  tgs = calloc(1, sizeof(*tgs));

  return tgs;
}


static void
tgState_destroy (TgState *tgs)
{
  assert( tgs != nullptr );

  free(tgs);
}


static void
tgState_generate (TgState *tgs, TgConfig const *cfg)
{
  assert( tgs != nullptr );
  assert( cfg != nullptr );

  tgs->cfg = cfg;

  for (size_t i = 0; i < cfg->num_tasks; ++i) {
    TgTask task = { .priority = i };

#if 1-1
    task->wcet = ...;
    task->initial_offset = ...;
    task->min_period = ...;
    task->rel_deadlin = ...;
#endif

    tgs->tasks[i] = task;
  }
}


static void
tgState_output (TgState *tgs)
{
  auto cfg = tgs->cfg;

  printf("%zu %zu\n", cfg->max_duration, cfg->num_tasks);

  for (TgTask const *task = &tgs->tasks[0];
       task < &tgs->tasks[cfg->num_tasks]; ++task)
    /* XXX: orer */
    printf("%zu %zu %zu %zu %zu",
     task->priority,
     task->initial_offset,
     task->min_period,
     task->rel_deadline,
     task->wcet);
}


int
main (int argc, char *argv[argc])
{
  (void) argc;
  (void) argv;

  TgConfig cfg = { 0 };
  tgConfig_fromCmdArgs((size_t)(argc),
   (char const **)(argv), &cfg);

  TgState *tgs = nullptr;
  tgs = tgState_new();

  tgState_generate(tgs, &cfg);
  tgState_output(tgs);

  tgState_destroy(tgs);

  return EXIT_SUCCESS;
}

