---
name: intervai-fe
description: Use for IntervAI frontend work in React/TypeScript/Vite, including pages, components, hooks, API clients, query state, routing, styling, or frontend docs.
---

# IntervAI Frontend Skill

Use this skill when changing frontend code or frontend-facing contracts.

## Scope

- Frontend app under `front/`
- React pages, components, hooks, API clients, stores, routing, and styling
- Frontend docs such as `front/README.md`, `front/HISTORY.md`, and frontend contract notes
- API alignment with `docs/api.md`

## Project Context

- Stack: Vite, React 19, TypeScript strict, TanStack Query v5, Zustand v5, axios, react-router-dom v7, react-hook-form, zod, Tailwind CSS.
- Package scripts are defined in `front/package.json`.
- The frontend should follow existing feature folder boundaries and shared component conventions.

## Frontend Architecture Rules

- Keep feature code under `front/src/features/{domain}` unless the code is truly shared.
- Keep shared layout, API utilities, and generic UI under `front/src/shared`.
- Use existing API clients, hooks, query keys, and error utilities instead of duplicating request logic.
- Keep TanStack Query keys centralized when a query key helper already exists.
- Match request and response types to `docs/api.md`; do not silently diverge from backend contracts.
- Keep UI work practical and task-focused. Avoid marketing-style screens unless the product explicitly needs one.
- Check responsive behavior for pages and components that render user-visible layouts.

## Workflow

1. Read the relevant docs first: `front/README.md`, `front/HISTORY.md` when historical context matters, and `docs/api.md` for backend contracts.
2. Inspect nearby components and hooks with `rg` before adding new patterns.
3. Implement within the smallest feature boundary that fits the change.
4. Update types and API clients together when a contract changes.
5. Run frontend verification from `front/`.

## Checks

Use these commands from `front/`:

```bash
npm run typecheck
npm run lint
npm run build
```

For ordinary feature work, run at least `npm run build` before shipping unless dependencies or environment constraints block it.
