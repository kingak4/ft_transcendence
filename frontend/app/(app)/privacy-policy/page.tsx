import ContactBlock from '../../components/ContactBlock';
import LegalSection from '../../components/LegalSection';

export default function PrivacyPolicy() {
  return (
    <main className="text mx-auto max-w-4xl px-6 py-12">
      <h1 className="mb-8 text-4xl font-bold">Privacy Policy</h1>

      <LegalSection title="1. Introduction">
        <p>
          This Privacy Policy explains how ft_transcendence collects, uses, and
          protects user information.
        </p>
        <p className="mt-4">
          ft_transcendence is a student project created as part of the 42 Warsaw
          curriculum.
        </p>
      </LegalSection>

      <LegalSection title="2. Data We Collect">
        <p>The application may collect the following information:</p>

        <ul className="mt-4 list-disc space-y-2 pl-6">
          <li>Username and profile information</li>
          <li>Email address (if provided)</li>
          <li>Authentication data</li>
          <li>Game statistics and match history</li>
          <li>User uploaded profile images</li>
          <li>Technical information such as IP address and browser type</li>
          <li>Session and cookie data required for authentication</li>
        </ul>
      </LegalSection>

      <LegalSection title="3. How We Use Data">
        <p>
          Collected data is used only for purposes related to the functionality
          of the application, including:
        </p>

        <ul className="mt-4 list-disc space-y-2 pl-6">
          <li>User authentication and account management</li>
          <li>Providing multiplayer game features</li>
          <li>Displaying rankings and match history</li>
          <li>Improving application stability and security</li>
          <li>Preventing abuse and unauthorized access</li>
        </ul>
      </LegalSection>

      <LegalSection title="4. Cookies">
        <p>
          ft_transcendence uses cookies and session storage to maintain user
          authentication and ensure proper functionality of the application.
        </p>

        <p className="mt-4">
          Users can disable cookies in their browser settings, although some
          features of the application may stop working properly.
        </p>
      </LegalSection>

      <LegalSection title="5. Data Storage and Security">
        <p>
          We take reasonable technical measures to protect user data against
          unauthorized access, modification, disclosure, or destruction.
        </p>

        <p className="mt-4">
          However, no internet transmission or electronic storage method is
          completely secure.
        </p>
      </LegalSection>

      <LegalSection title="6. User Rights">
        <p>
          Users may request access to their personal data, correction of
          inaccurate information, or deletion of their account and associated
          data.
        </p>
      </LegalSection>

      <LegalSection title="7. Third-Party Services">
        <p>
          ft_transcendence may use third-party authentication or hosting
          services required for application functionality.
        </p>

        <p className="mt-4">
          These services may process data according to their own privacy
          policies.
        </p>
      </LegalSection>

      <LegalSection title="8. Contact">
        <p>
          If you have questions regarding this Privacy Policy, you may contact
          the project administrators:
        </p>

        <ContactBlock className="mt-4" />
      </LegalSection>
    </main>
  );
}
