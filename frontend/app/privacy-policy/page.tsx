export default function PrivacyPolicy() {
  return (
    <main className="mx-auto max-w-4xl px-6 py-12 text-white">
      <h1 className="mb-8 text-4xl font-bold">Privacy Policy</h1>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">1. Introduction</h2>
        <p>
          This Privacy Policy explains how ft_transcendence collects, uses, and
          protects user information.
        </p>
        <p className="mt-4">
          ft_transcendence is a student project created as part of the 42 Warsaw
          curriculum.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">2. Data We Collect</h2>

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
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">3. How We Use Data</h2>

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
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">4. Cookies</h2>

        <p>
          ft_transcendence uses cookies and session storage to maintain user
          authentication and ensure proper functionality of the application.
        </p>

        <p className="mt-4">
          Users can disable cookies in their browser settings, although some
          features of the application may stop working properly.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">
          5. Data Storage and Security
        </h2>

        <p>
          We take reasonable technical measures to protect user data against
          unauthorized access, modification, disclosure, or destruction.
        </p>

        <p className="mt-4">
          However, no internet transmission or electronic storage method is
          completely secure.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">6. User Rights</h2>

        <p>
          Users may request access to their personal data, correction of
          inaccurate information, or deletion of their account and associated
          data.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">7. Third-Party Services</h2>

        <p>
          ft_transcendence may use third-party authentication or hosting
          services required for application functionality.
        </p>

        <p className="mt-4">
          These services may process data according to their own privacy
          policies.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="mb-4 text-2xl font-semibold">8. Contact</h2>

        <p>
          If you have questions regarding this Privacy Policy, you may contact
          the project administrators:
        </p>

        <div className="mt-4 space-y-2">
          <p>ft_transcendence</p>
          <p>42 Warsaw</p>
          <p>Al. Solidarności 171B</p>
          <p>00-877 Warszawa</p>
          <p>Poland</p>
          <p>Email: kinga.kwasniak5@gmail.com</p>
        </div>
      </section>
    </main>
  );
}
