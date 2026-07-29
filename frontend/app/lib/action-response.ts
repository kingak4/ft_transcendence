// The shape returned by the `login` and `register` server actions.
//
// Deliberately *not* housed in either of those modules: both are `'use server'`
// files, whose exports are the server-action boundary. A type is not an action,
// and whichever of the two owned it would make the other import from a sibling
// for no reason other than that it was declared there first.
export type ActionResponse = {
  success: boolean;
  status?: number;
  message?: string;
};
