# Witness Infrastructure

Phase 2 of the Witness system — a longitudinal crystallization tracker and Catalytic Living Interface for attention signature analysis.

## What This Is

Witness Infrastructure receives behavioral data from [Witness Android](https://github.com/hillarynjuguna/witness-android) (scroll pause signatures) and processes it through a crystallization model. The goal: identify when attention patterns shift from reactive scrolling to standing-wave engagement — the signature of genuine field resonance vs. compulsive consumption.

Phase 2 provides the data pipeline, schema, and temporal analysis utilities. Phase 3 will add the Catalytic Living Interface — a real-time feedback layer that surfaces crystallization events as they occur.

## Status

Phase 2 — Active Development

## Architecture

```
src/types/schema.ts     Crystallization event schema (Zod)
src/utils/temporal.ts   Temporal analysis: pause window detection, standing wave calc
```

**Stack:** TypeScript, Zod, Node.js

## Related

- [Witness Android](https://github.com/hillarynjuguna/witness-android) — Phase 1: data collection (accessibility service + SQLite)
- [DCFB](https://github.com/hillarynjuguna/dcfb) — Theoretical substrate for what crystallization measures
- [Intelligence Site](https://hillary-site.vercel.app) — Published research context
