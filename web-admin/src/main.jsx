import ReactDOM from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { Buffer } from 'buffer'
import App from './App.jsx'
import './index.css'

// Polyfill Buffer for music-metadata-browser
window.Buffer = Buffer

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
})

ReactDOM.createRoot(document.getElementById('root')).render(
  <QueryClientProvider client={queryClient}>
    <BrowserRouter>
      <App />
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#1a1a1a',
            color: '#fff',
            border: '1px solid #282828',
            borderRadius: '0.75rem',
            padding: '16px',
          },
          success: {
            iconTheme: {
              primary: '#1DB954',
              secondary: '#fff',
            },
          },
          error: {
            iconTheme: {
              primary: '#FA243C',
              secondary: '#fff',
            },
          },
        }}
      />
    </BrowserRouter>
  </QueryClientProvider>,
)
