// Language-neutral facts about each public endpoint, mirrored from
// app/types/api.d.ts. Human-readable text lives in messages/*.json under
// apiDocs.endpoints.<id> and apiDocs.params.<name>.

export type HttpMethod = 'GET' | 'POST' | 'PATCH' | 'DELETE';

export type ParamName = 'friendId' | 'page' | 'size' | 'query' | 'displayName';

export type StatusCode = 200 | 201 | 204 | 400 | 401 | 403 | 404 | 500;

export interface EndpointParam {
  name: ParamName;
  location: 'path' | 'query' | 'body';
  type: string;
  required: boolean;
}

export interface Endpoint {
  id: string;
  method: HttpMethod;
  path: string;
  params: readonly EndpointParam[];
  requestExample?: string;
  responseExample?: string;
  statuses: readonly StatusCode[];
}

const json = (value: unknown) => JSON.stringify(value, null, 2);

const SAMPLE_USER_ID = '3f2b8c1e-7a4d-4e2b-9c61-5d8e0f1a2b3c';
const SAMPLE_AVATAR_ID = 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d';

const PAGINATION_PARAMS: readonly EndpointParam[] = [
  { name: 'page', location: 'query', type: 'integer', required: false },
  { name: 'size', location: 'query', type: 'integer', required: false },
];

export const ENDPOINTS = [
  {
    id: 'listFriends',
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
  },
  {
    id: 'addFriend',
    method: 'POST',
    path: '/api/friends/{friendId}',
    params: [
      { name: 'friendId', location: 'path', type: 'uuid', required: true },
    ],
    statuses: [201, 400, 401, 403, 404, 500],
  },
  {
    id: 'removeFriend',
    method: 'DELETE',
    path: '/api/friends/{friendId}',
    params: [
      { name: 'friendId', location: 'path', type: 'uuid', required: true },
    ],
    statuses: [204, 400, 401, 403, 404, 500],
  },
  {
    id: 'updateDisplayName',
    method: 'PATCH',
    path: '/api/users/display-name',
    params: [
      { name: 'displayName', location: 'body', type: 'string', required: true },
    ],
    requestExample: json({ displayName: 'marvin' }),
    statuses: [200, 400, 401, 403, 404, 500],
  },
  {
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
  },
] as const satisfies readonly Endpoint[];

export type EndpointId = (typeof ENDPOINTS)[number]['id'];

export const ERROR_EXAMPLE = json({
  type: 'about:blank',
  title: 'Not Found',
  status: 404,
  detail: 'User not found',
  instance: `/friends/${SAMPLE_USER_ID}`,
});
