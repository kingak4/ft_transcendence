import { forwardRef, useState } from 'react';
import Button from '../../components/Button';
import TextField from '../../components/TextField';
import { chatMessageSchema } from '../../lib/validation/chat';
import { CHAT_DICT } from './dictionary';

interface Props {
  inputValue: string;
  setInputValue: (value: string) => void;
  handleSend: (e: React.FormEvent) => void;
  isInputDisabled?: boolean;
  isButtonDisabled?: boolean;
  errorMsg?: string | null;
}

const Composer = forwardRef<HTMLInputElement, Props>(function Composer(
  {
    inputValue,
    setInputValue,
    handleSend,
    isInputDisabled,
    isButtonDisabled,
    errorMsg,
  },
  ref,
) {
  // The parent owns the value, so validation stays a local check in front of
  // `handleSend` rather than a form library taking the value over. The
  // schema's message wins over the parent's: if the input is invalid, no send
  // happened, so any parent error on screen is stale.
  const [validationError, setValidationError] = useState<string | null>(null);
  const shownError = validationError ?? errorMsg;

  function handleSubmit(e: React.FormEvent) {
    const result = chatMessageSchema.safeParse(inputValue);
    if (!result.success) {
      e.preventDefault();
      setValidationError(result.error.issues[0].message);
      return;
    }
    setValidationError(null);
    handleSend(e);
  }

  return (
    <div className="bg-elevated-surface border-elevated-border flex shrink-0 flex-col border-t p-4">
      {shownError && (
        <div className="text-danger mb-2 text-sm">
          {shownError}
        </div>
      )}
      <form
        onSubmit={handleSubmit}
        className="flex items-center gap-3"
      >
      <TextField
        ref={ref}
        tone="chat"
        placeholder={CHAT_DICT.composer.placeholder}
        aria-label={CHAT_DICT.composer.ariaLabel}
        className="flex-1"
        value={inputValue}
        onChange={(e) => {
          setInputValue(e.target.value);
          setValidationError(null);
        }}
        disabled={!!isInputDisabled}
        suppressHydrationWarning
      />
      <Button disabled={!!isButtonDisabled} type="submit" suppressHydrationWarning>
        {CHAT_DICT.composer.send}
      </Button>
    </form>
    </div>
  );
});

export default Composer;
