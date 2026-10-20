import { defineConfig } from 'eslint/config';
import nextVitals from 'eslint-config-next/core-web-vitals';
import nextTs from 'eslint-config-next/typescript';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import reactCompiler from 'eslint-plugin-react-compiler';

export default defineConfig([
  ...nextVitals,
  ...nextTs,
  {
    plugins: {
      'react-compiler': reactCompiler,
    },
    rules: {
      // nextVitals already registers the jsx-a11y plugin; reuse it, only raise the rules.
      ...jsxA11y.flatConfigs.strict.rules,
      // Scrollable <main> and the chat message region must be focusable (WCAG 2.1.1).
      'jsx-a11y/no-noninteractive-tabindex': [
        'error',
        { tags: ['main'], roles: ['region'] },
      ],

      'react-compiler/react-compiler': 'error',

      '@typescript-eslint/no-unused-vars': 'warn',
      '@typescript-eslint/no-explicit-any': 'warn',
    },
  },
]);