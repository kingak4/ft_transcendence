import { forwardRef } from 'react';
import Button from '../../components/Button';
import TextField from '../../components/TextField';

interface Props {
  inputValue: string;
  setInputValue: (value: string) => void;
  handleSend: (e: React.FormEvent) => void;
  isInputDisabled?: boolean;
  isButtonDisabled?: boolean;
  errorMsg?: string | null;
}

const Composer = forwardRef<HTMLInputElement, Props>(function Composer({ inputValue, setInputValue, handleSend, isInputDisabled, isButtonDisabled, errorMsg }, ref) {
  return (
    <div className="bg-hub-panel border-hub-border flex shrink-0 flex-col border-t p-4">
      {errorMsg && (
        <div className="text-red-500 text-sm mb-2">
          {errorMsg}
        </div>
      )}
      <form 
        onSubmit={handleSend}
        className="flex items-center gap-3"
      >
      <TextField
        ref={ref}
        tone="chat"
        placeholder="Type a message"
        aria-label="Message"
        className="flex-1"
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        disabled={!!isInputDisabled}
        suppressHydrationWarning
      />
      <Button variant="send" disabled={!!isButtonDisabled} type="submit" suppressHydrationWarning>
        Send
      </Button>
    </form>
    </div>
  );
});

export default Composer;
