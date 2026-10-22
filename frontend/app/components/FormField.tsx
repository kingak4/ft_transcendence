import { type ComponentPropsWithoutRef, forwardRef, useId } from 'react';

import TextField from './TextField';

type FormFieldProps = ComponentPropsWithoutRef<typeof TextField> & {
  label: string;
  error?: string;
};

const FormField = forwardRef<HTMLInputElement, FormFieldProps>(
  function FormField({ label, error, id, className = '', ...fieldProps }, ref) {
    const fallbackId = useId();
    const inputId = id ?? fallbackId;
    const errorId = `${inputId}-error`;

    return (
      <div className={className}>
        <label htmlFor={inputId} className="mb-1.5 block text-xs font-semibold">
          {label}
        </label>
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
