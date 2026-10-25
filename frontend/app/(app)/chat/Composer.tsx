import { useTranslations } from 'next-intl';
import { forwardRef, useId, useState } from 'react';
import Button from '../../components/Button';
import Icon from '../../components/Icon';
import TextField from '../../components/TextField';
import { useValidationMessage } from '../../hooks/useValidationMessage';
import { chatMessageSchema } from '../../lib/validation/chat';

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
  const t = useTranslations('chat.composer');
  const validationMessage = useValidationMessage();
  const [validationError, setValidationError] = useState<string | null>(null);
  const shownError = validationError ?? errorMsg;
  const errorId = useId();

  function handleSubmit(e: React.FormEvent) {
    const result = chatMessageSchema.safeParse(inputValue);
    if (!result.success) {
      e.preventDefault();
      setValidationError(
        validationMessage(result.error.issues[0].message) ?? null,
      );
      return;
    }
    setValidationError(null);
    handleSend(e);
  }

  return (
    <div className="bg-elevated-surface border-elevated-border flex shrink-0 flex-col border-t p-4">
      {shownError && (
        <div id={errorId} role="alert" className="text-danger mb-2 text-sm">
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
        dir="auto"
        placeholder={t('placeholder')}
        aria-label={t('ariaLabel')}
        aria-invalid={!!validationError}
        aria-describedby={shownError ? errorId : undefined}
        className="flex-1"
        value={inputValue}
        onChange={(e) => {
          setInputValue(e.target.value);
          setValidationError(null);
        }}
        disabled={!!isInputDisabled}
        suppressHydrationWarning
      />
      <Button
        disabled={!!isButtonDisabled}
        type="submit"
        aria-label={t('send')}
        suppressHydrationWarning
      >
        <Icon name="send" size="md" mirrorInRtl />
      </Button>
    </form>
    </div>
  );
});

export default Composer;
