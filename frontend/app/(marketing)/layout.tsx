import BareLayout from '../components/BareLayout';

export default function MarketingLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <BareLayout>{children}</BareLayout>;
}
