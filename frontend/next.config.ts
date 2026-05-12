import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        // When I fetch something starting with /api/
        source: "/api/:path*",
        // Send it to my backend on 8080
        destination: "http://localhost:8080/:path*",
      },
    ];
  },
  reactCompiler: true,
};

export default nextConfig;
