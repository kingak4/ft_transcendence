FROM node:20-alpine AS builder
WORKDIR /build

ARG BACKEND_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /frontend

ENV NODE_ENV=production

COPY --from=builder /build/.next/standalone ./
COPY --from=builder /build/.next/static ./.next/static

EXPOSE 3000
ENTRYPOINT ["node", "server.js"]