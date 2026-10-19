import { type ComponentPropsWithoutRef, forwardRef, useId } from 'react';

import TextField from './TextField';

type FormFieldProps = ComponentPropsWithoutRef<typeof TextField> & {
  error?: string;
};

// A TextField plus the message explaining why it is invalid. TextField keeps
// styling as its only job; this owns the link between a field and its error,
// so a screen reader announces the message when the input is focused.
//
// `className` moves to the wrapper, so a layout margin passed by the call site
// sits below the error rather than between the input and it.
const FormField = forwardRef<HTMLInputElement, FormFieldProps>(
  function FormField({ error, id, className = '', ...fieldProps }, ref) {
    const fallbackId = useId();
    const inputId = id ?? fallbackId;
    const errorId = `${inputId}-error`;

    return (
      <div className={className}>
        <TextField
          ref={ref}
          id={inputId}
          {...fieldProps}
          aria-invalid={error ? true : undefined}
          aria-describedby={error ? errorId : undefined}
        />
        {error && (
          <p id={errorId} className="text-danger mt-1 text-sm">
            {error}
          </p>
        )}
      </div>
    );
  },
);

export default FormField;
