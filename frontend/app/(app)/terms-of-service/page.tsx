import ContactBlock from '../../components/ContactBlock';
import LegalSection from '../../components/LegalSection';

export default function TermsOfService() {
  return (
    <main className="text mx-auto max-w-4xl px-6 py-12">
      <h1 className="mb-8 text-4xl font-bold">Terms of Service</h1>

      <p className="mb-6">Last updated: May 2026</p>

      <LegalSection title="1. Introduction">
        <p>Welcome to ft_transcendence.</p>

        <p className="mt-4">
          By accessing or using this application, you agree to comply with these
          Terms of Service.
        </p>

        <p className="mt-4">
          ft_transcendence is a student project created as part of the 42 Warsaw
          curriculum.
        </p>
      </LegalSection>

      <LegalSection title="2. User Accounts">
        <p>
          Users are responsible for maintaining the security of their accounts
          and authentication credentials.
        </p>

        <p className="mt-4">
          You agree not to impersonate other users or attempt unauthorized
          access to accounts, systems, or data.
        </p>
      </LegalSection>

      <LegalSection title="3. Acceptable Use">
        <p>By using the application, you agree not to:</p>

        <ul className="mt-4 list-disc space-y-2 pl-6">
          <li>Use the platform for illegal activities</li>
          <li>Exploit vulnerabilities or security issues</li>
          <li>Disrupt servers, matchmaking, or gameplay</li>
          <li>Upload malicious or harmful content</li>
          <li>Harass, abuse, or threaten other users</li>
          <li>Attempt unauthorized access to the system</li>
        </ul>
      </LegalSection>

      <LegalSection title="4. Intellectual Property">
        <p>
          All project content, source code, branding, interface elements, and
          application assets remain the property of their respective creators
          unless otherwise stated.
        </p>
      </LegalSection>

      <LegalSection title="5. Service Availability">
        <p>
          The application is provided on an &quot;as is&quot; and &quot;as
          available&quot; basis.
        </p>

        <p className="mt-4">
          We do not guarantee uninterrupted availability, error-free operation,
          or permanent access to the service.
        </p>
      </LegalSection>

      <LegalSection title="6. Limitation of Liability">
        <p>
          The creators of ft_transcendence are not liable for any damages, data
          loss, interruptions, or issues resulting from the use of the
          application.
        </p>
      </LegalSection>

      <LegalSection title="7. Account Termination">
        <p>
          We reserve the right to suspend or terminate user accounts that
          violate these Terms of Service or compromise the security and
          stability of the application.
        </p>
      </LegalSection>

      <LegalSection title="8. Changes to Terms">
        <p>
          These Terms of Service may be updated or modified at any time without
          prior notice.
        </p>

        <p className="mt-4">
          Continued use of the application after changes constitutes acceptance
          of the updated terms.
        </p>
      </LegalSection>

      <LegalSection title="9. Contact">
        <ContactBlock />
      </LegalSection>
    </main>
  );
}
