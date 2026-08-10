import ContactBlock from '../../components/ContactBlock';
import LegalSection from '../../components/LegalSection';

export default function PrivacyPolicy() {
  return (
    // Same card as terms-of-service, and the same reasons - see the comment
    // there. The two pages are one decision applied twice, not two decisions.
    //
    // One difference worth noticing rather than fixing: this page has no
    // "Last updated" line, while terms-of-service does and the export gives one
    // to both. That is missing content, not missing styling (12.5).
    <div className="bg-elevated-surface text-on-elevated-surface mx-auto flex max-w-[720px] flex-col gap-1.5 rounded-3xl p-6 text-base font-medium leading-relaxed shadow-[0_8px_24px_rgba(10,42,77,0.08)] lg:px-14 lg:py-12">
      <h1 className="text-2xl font-extrabold tracking-tight lg:text-3xl">
        Privacy Policy
      </h1>

      <LegalSection title="1. Introduction">
        <p>
          This Privacy Policy explains how ft_transcendence collects, uses, and
          protects user information.
        </p>
        <p>
          ft_transcendence is a student project created as part of the 42 Warsaw
          curriculum.
        </p>
      </LegalSection>

      <LegalSection title="2. Data We Collect">
        <p>The application may collect the following information:</p>

        <ul className="mt-1 list-disc space-y-2 ps-5">
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

        <ul className="mt-1 list-disc space-y-2 ps-5">
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

        <p>
          Users can disable cookies in their browser settings, although some
          features of the application may stop working properly.
        </p>
      </LegalSection>

      <LegalSection title="5. Data Storage and Security">
        <p>
          We take reasonable technical measures to protect user data against
          unauthorized access, modification, disclosure, or destruction.
        </p>

        <p>
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

        <p>
          These services may process data according to their own privacy
          policies.
        </p>
      </LegalSection>

      <LegalSection title="8. Contact">
        <p>
          If you have questions regarding this Privacy Policy, you may contact
          the project administrators:
        </p>

        <ContactBlock />
      </LegalSection>
    </div>
  );
}
