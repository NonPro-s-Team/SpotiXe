/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        // Primary Brand Colors
        spotify: {
          green: "#1DB954",
          "green-dark": "#1AA34A",
          "green-light": "#1ED760",
        },
        apple: {
          red: "#FA243C",
          blue: "#0A84FF",
        },
        // Background Colors
        bg: {
          primary: "#0a0a0a",
          secondary: "#121212",
          tertiary: "#1a1a1a",
          hover: "#282828",
          card: "#1a1a1a",
        },
        // Text Colors
        text: {
          primary: "#FFFFFF",
          secondary: "#B3B3B3",
          tertiary: "#6A6A6A",
          disabled: "#3E3E3E",
        },
        // Border Colors
        border: {
          DEFAULT: "#282828",
          hover: "#3E3E3E",
          active: "#1DB954",
        },
        // Additional Colors
        purple: "#BF5AF2",
        accent: {
          orange: "#FF6B35",
          yellow: "#FFD23F",
          pink: "#FF10F0",
          cyan: "#00D9FF",
        },
      },
      fontFamily: {
        sans: [
          "Inter",
          "system-ui",
          "-apple-system",
          "BlinkMacSystemFont",
          "Segoe UI",
          "sans-serif",
        ],
        display: ["SF Pro Display", "Inter", "sans-serif"],
      },
      fontSize: {
        xs: ["0.75rem", { lineHeight: "1rem" }],
        sm: ["0.875rem", { lineHeight: "1.25rem" }],
        base: ["1rem", { lineHeight: "1.5rem" }],
        lg: ["1.125rem", { lineHeight: "1.75rem" }],
        xl: ["1.25rem", { lineHeight: "1.75rem" }],
        "2xl": ["1.5rem", { lineHeight: "2rem" }],
        "3xl": ["1.875rem", { lineHeight: "2.25rem" }],
        "4xl": ["2.25rem", { lineHeight: "2.5rem" }],
        "5xl": ["3rem", { lineHeight: "1" }],
      },
      spacing: {
        18: "4.5rem",
        88: "22rem",
        128: "32rem",
      },
      borderRadius: {
        xl: "1rem",
        "2xl": "1.5rem",
        "3xl": "2rem",
      },
      boxShadow: {
        "glow-sm": "0 0 10px rgba(29, 185, 84, 0.3)",
        glow: "0 0 20px rgba(29, 185, 84, 0.4)",
        "glow-lg": "0 0 30px rgba(29, 185, 84, 0.5)",
        card: "0 4px 6px -1px rgba(0, 0, 0, 0.3), 0 2px 4px -1px rgba(0, 0, 0, 0.2)",
        "card-hover":
          "0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.3)",
      },
      backgroundImage: {
        "gradient-primary": "linear-gradient(135deg, #1DB954 0%, #0A84FF 100%)",
        "gradient-purple": "linear-gradient(135deg, #BF5AF2 0%, #FF10F0 100%)",
        "gradient-orange": "linear-gradient(135deg, #FF6B35 0%, #FFD23F 100%)",
        "gradient-dark": "linear-gradient(180deg, #1a1a1a 0%, #0a0a0a 100%)",
        shimmer:
          "linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.05), transparent)",
      },
      animation: {
        shimmer: "shimmer 2s infinite",
        "fade-in": "fadeIn 0.2s ease-in-out",
        "slide-in": "slideIn 0.2s ease-out",
        "scale-in": "scaleIn 0.2s ease-out",
        "pulse-slow": "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
      },
      keyframes: {
        shimmer: {
          "0%": { transform: "translateX(-100%)" },
          "100%": { transform: "translateX(100%)" },
        },
        fadeIn: {
          "0%": { opacity: "0" },
          "100%": { opacity: "1" },
        },
        slideIn: {
          "0%": { transform: "translateY(-10px)", opacity: "0" },
          "100%": { transform: "translateY(0)", opacity: "1" },
        },
        scaleIn: {
          "0%": { transform: "scale(0.95)", opacity: "0" },
          "100%": { transform: "scale(1)", opacity: "1" },
        },
      },
      backdropBlur: {
        xs: "2px",
      },
    },
  },
  plugins: [],
};
