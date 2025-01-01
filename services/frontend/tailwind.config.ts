import type { Config } from 'tailwindcss';
import typography from '@tailwindcss/typography';

export default {
	content: ['./src/**/*.{html,js,svelte,ts}'],
	darkMode: 'class', // or 'media' for OS-level dark mode
	theme: {
		extend: {
			colors: {
				background: 'hsl(var(--background))',
				'background-alt': 'hsl(var(--background-alt))',
				muted: 'hsl(var(--muted))',
				'muted-foreground': 'hsl(var(--muted-foreground))',
				border: 'hsl(var(--border))',
				'border-input': 'hsl(var(--border-input))',
				'border-input-hover': 'hsl(var(--border-input-hover))',
				'border-card': 'hsl(var(--border-card))',
				accent: 'hsl(var(--accent))',
				'accent-foreground': 'hsl(var(--accent-foreground))',
				destructive: 'hsl(var(--destructive))',
				contrast: 'hsl(var(--contrast))',
				foreground: {
					DEFAULT: 'hsl(var(--foreground))',
					'100': 'hsl(var(--foreground))',
					'90': 'hsl(var(--foreground)/0.9)',
					'80': 'hsl(var(--foreground)/0.8)',
					'70': 'hsl(var(--foreground)/0.7)',
					'60': 'hsl(var(--foreground)/0.6)',
					'50': 'hsl(var(--foreground)/0.5)',
					'40': 'hsl(var(--foreground)/0.4)',
					'30': 'hsl(var(--foreground)/0.3)',
					'20': 'hsl(var(--foreground)/0.2)',
					'10': 'hsl(var(--foreground)/0.1)',
					'04': 'hsl(var(--foreground)/0.04)'
				}
			},
			height: {
				"input": "var(--input-height)"
			},
			boxShadow: {
				mini: 'var(--shadow-mini)',
				'mini-inset': 'var(--shadow-mini-inset)',
				popover: 'var(--shadow-popover)',
				kbd: 'var(--shadow-kbd)',
				btn: 'var(--shadow-btn)',
				card: 'var(--shadow-card)',
				'date-field-focus': 'var(--shadow-date-field-focus)'
			}
		}
	},

	plugins: [typography]
} satisfies Config;
