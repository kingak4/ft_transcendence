// Language-neutral facts about each public endpoint, type-checked against
// app/types/api.d.ts: ids are backend operationIds, and paths and statuses
// must exist there, so regenerating the schema breaks the build on drift.
// Human-readable text lives in messages/*.json under apiDocs.endpoints.<id>
// and apiDocs.params.<name>.

import type { operations, paths } from '../../types/api';

export type HttpMethod = 'GET' | 'POST' | 'PATCH' | 'DELETE';

export type ParamName = 'friendId' | 'page' | 'size' | 'query' | 'displayName';

type OperationId = keyof operations;

type StatusOf<Op extends OperationId> = keyof operations[Op]['responses'] &
  number;

// nginx exposes every backend path under /api (prefix-stripping).
type DocumentedPath = `/api${keyof paths & string}`;

export interface EndpointParam {
  name: ParamName;
  location: 'path' | 'query' | 'body';
  type: string;
  required: boolean;
}

export interface Endpoint<Op extends OperationId = OperationId> {
  id: Op;
  method: HttpMethod;
  path: DocumentedPath;
  params: readonly EndpointParam[];
  requestExample?: string;
  responseExample?: string;
  statuses: readonly StatusOf<Op>[];
}

// Identity helper: lets each entry infer its own Op from `id`, so its
// statuses are checked against that operation's responses only.
function defineEndpoint<const Op extends OperationId>(
  endpoint: Endpoint<Op>,
): Endpoint<Op> {
  return endpoint;
}

const json = (value: unknown) => JSON.stringify(value, null, 2);

const SAMPLE_USER_ID = '3f2b8c1e-7a4d-4e2b-9c61-5d8e0f1a2b3c';
const SAMPLE_AVATAR_ID = 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d';

const PAGINATION_PARAMS: readonly EndpointParam[] = [
  { name: 'page', location: 'query', type: 'integer', required: false },
  { name: 'size', location: 'query', type: 'integer', required: false },
];

export const ENDPOINTS = [
  defineEndpoint({
    id: 'getFriendList',
    method: 'GET',
    path: '/api/friends',
    params: PAGINATION_PARAMS,
    responseExample: json({
      content: [
        {
          id: SAMPLE_USER_ID,
          details: {
            displayName: 'marvin',
            avatarId: { val: SAMPLE_AVATAR_ID },
          },
        },
      ],
      totalElements: 1,
      totalPages: 1,
      number: 0,
      size: 20,
      first: true,
      last: true,
    }),
    statuses: [200, 400, 401, 403, 500],
  }),
  defineEndpoint({
    id: 'addFriend',
    method: 'POST',
    path: '/api/friends/{friendId}',
    params: [
      { name: 'friendId', location: 'path', type: 'uuid', required: true },
    ],
    statuses: [201, 400, 401, 403, 404, 500],
  }),
  defineEndpoint({
    id: 'removeFriend',
    method: 'DELETE',
    path: '/api/friends/{friendId}',
    params: [
      { name: 'friendId', location: 'path', type: 'uuid', required: true },
    ],
    statuses: [204, 400, 401, 403, 404, 500],
  }),
  defineEndpoint({
    id: 'updateDisplayName',
    method: 'PATCH',
    path: '/api/users/display-name',
    params: [
      { name: 'displayName', location: 'body', type: 'string', required: true },
    ],
    requestExample: json({ displayName: 'marvin' }),
    statuses: [200, 400, 401, 403, 404, 500],
  }),
  defineEndpoint({
    id: 'searchUsers',
    method: 'GET',
    path: '/api/users/search',
    params: [
      { name: 'query', location: 'query', type: 'string', required: true },
      ...PAGINATION_PARAMS,
    ],
    responseExample: json({
      content: [
        {
          id: SAMPLE_USER_ID,
          displayName: 'marvin',
          avatarId: { val: SAMPLE_AVATAR_ID },
        },
      ],
      totalElements: 1,
      totalPages: 1,
      number: 0,
      size: 20,
      first: true,
      last: true,
    }),
    statuses: [200, 400, 401, 403, 404, 500],
  }),
] as const;

export type EndpointId = (typeof ENDPOINTS)[number]['id'];

export type StatusCode = (typeof ENDPOINTS)[number]['statuses'][number];

export const ERROR_EXAMPLE = json({
  type: 'about:blank',
  title: 'Not Found',
  status: 404,
  detail: 'User not found',
  instance: `/friends/${SAMPLE_USER_ID}`,
});
