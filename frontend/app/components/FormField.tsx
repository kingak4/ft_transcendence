import { type ComponentPropsWithoutRef, forwardRef, useId } from 'react';

import TextField from './TextField';

type FormFieldProps = ComponentPropsWithoutRef<typeof TextField> & {
  error?: string;
};

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
