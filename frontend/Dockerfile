FROM node:20-alpine AS builder
WORKDIR /build

ARG BACKEND_URL
ARG NEXT_PUBLIC_WS_URL
ENV BACKEND_URL=$BACKEND_URL
ENV NEXT_PUBLIC_WS_URL=$NEXT_PUBLIC_WS_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /frontend

ENV NODE_ENV=production

COPY --from=builder /build/.next/standalone ./
COPY --from=builder /build/.next/static ./.next/static

ENTRYPOINT ["node", "server.js"]