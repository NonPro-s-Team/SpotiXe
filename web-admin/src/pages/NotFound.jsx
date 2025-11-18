import { Link } from 'react-router-dom'
import { Home, SearchX } from 'lucide-react'
import Button from '@components/common/Button'
import { motion } from 'framer-motion'

export default function NotFound() {
  return (
    <div className="min-h-screen bg-bg-primary flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="text-center"
      >
        <div className="mb-8">
          <SearchX className="w-24 h-24 mx-auto text-text-tertiary mb-6" />
          <h1 className="text-6xl font-bold text-gradient mb-4">404</h1>
          <h2 className="text-2xl font-semibold text-text-primary mb-2">
            Page Not Found
          </h2>
          <p className="text-text-secondary max-w-md mx-auto">
            The page you're looking for doesn't exist or has been moved.
          </p>
        </div>
        <Link to="/dashboard">
          <Button icon={Home} iconPosition="left">
            Back to Dashboard
          </Button>
        </Link>
      </motion.div>
    </div>
  )
}
