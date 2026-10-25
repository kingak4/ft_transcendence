import { useTranslations } from 'next-intl';

import Icon from '../../components/Icon';
import type { IconName } from '../../components/icons';

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
  const t = useTranslations('chat.status');

  return (
    <Icon
      name={STATUS_ICON[status]}
      size="xs"
      label={t(status)}
      className={status === 'failed' ? 'text-danger' : undefined}
    />
  );
}
