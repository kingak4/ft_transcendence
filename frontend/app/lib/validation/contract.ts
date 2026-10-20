// Compile-time check that a form's parsed values carry every field of the
// request body the backend expects, as generated into app/types/api.d.ts. If
// the backend renames a field and the types are regenerated, the alias using
// this stops compiling and points at the form that drifted.
export type Satisfies<T extends U, U> = T;
