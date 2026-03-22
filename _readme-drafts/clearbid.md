# ClearBid

Procurement intelligence built on the v-node principle: testimony precedes payment.

## What This Is

ClearBid is a procurement intelligence platform that inverts the standard vendor selection process. Instead of optimizing for the lowest bid, ClearBid structures procurement around verified testimony — documented evidence of outcomes from comparable engagements. The v-node principle: no payment node activates without a testimony node. This eliminates the principal-agent failure mode endemic to procurement.

The platform generates DealPackets — structured procurement documents that map vendor claims to verifiable testimony chains — and provides AI-assisted analysis of the testimony quality.

## Status

In Development — DealPacket generation complete; consultation booking and payment integration in progress.

## Architecture

```
src/lib/dealpacket/generator.ts   DealPacket generation (OpenRouter)
src/lib/dealpacket/schema.ts      DealPacket schema (Zod)
src/lib/nunode/transaction.ts     V-node transaction model
src/components/ConsultationBooking.tsx     Paid consultation flow (Lemon Squeezy)
src/components/DealPacketRequestForm.tsx   Free DealPacket request (Formspree)
```

**Stack:** Next.js, TypeScript, OpenRouter, Lemon Squeezy, Formspree, Tailwind

## Related

- [Premise](https://github.com/hillarynjuguna/premise) — Sibling product: decision quality engine
- [DCFB](https://github.com/hillarynjuguna/dcfb) — Shared theoretical substrate
- [Intelligence Site](https://hillary-site.vercel.app) — Published product page
