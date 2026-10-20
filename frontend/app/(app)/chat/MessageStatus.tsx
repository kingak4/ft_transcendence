import Icon from '../../components/Icon';
import type { IconName } from '../../components/icons';
import { CHAT_DICT } from './dictionary';

export type DeliveryStatus = 'sending' | 'sent' | 'failed';

const STATUS_ICON: Record<DeliveryStatus, IconName> = {
  sending: 'clock',
  sent: 'check',
  failed: 'alert',
};

interface Props {
  status: DeliveryStatus;
}

export default function MessageStatus({ status }: Props) {
  return (
    <Icon
      name={STATUS_ICON[status]}
      size="xs"
      label={CHAT_DICT.status[status]}
      className={status === 'failed' ? 'text-danger' : undefined}
    />
  );
}
