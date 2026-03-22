# Premise

Decision Quality Engine — a multi-tenant three-pass cognitive pipeline for organizational decision documents, structurally resistant to LLM failure modes.

## What This Is

Premise applies DCFB (Distributed Cognition Fear Bypass) analysis to organizational decision-making. Most AI-assisted decision tools fail at the same point: they optimize for the appearance of good decisions rather than their structural quality. Premise addresses this by running each decision document through three passes that map the fear topology, identify precision-weighting failures, and surface the bypass paths before the organization commits.

Multi-tenant. Built for decision committees, strategy teams, and organizations that cannot afford to discover their cognitive architecture failures after the fact.

## Status

In Development — core pipeline complete; multi-tenant auth and deployment in progress.

## Architecture

```
src/lib/llm.ts          Provider-agnostic LLM layer (OpenRouter)
src/lib/embeddings.ts   Semantic similarity for decision pattern matching
src/lib/inngest.ts      Async pipeline orchestration (three-pass processing)
src/lib/db.ts           Supabase + pgvector (decision history, pattern library)
src/types/schema.ts     Decision document schema (Zod)
```

**Stack:** Next.js, TypeScript, Supabase, pgvector, Inngest, OpenRouter

## Related

- [DCFB](https://github.com/hillarynjuguna/dcfb) — Theoretical substrate: the three-pass model
- [ClearBid](https://github.com/hillarynjuguna/clearbid) — Sibling product: procurement intelligence
- [Intelligence Site](https://hillary-site.vercel.app) — Published product page
