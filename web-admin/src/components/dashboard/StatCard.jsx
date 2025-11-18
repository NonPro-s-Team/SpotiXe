import { TrendingUp, TrendingDown } from 'lucide-react'
import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { cn, formatNumber } from '@utils/helpers'

export default function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  trendValue,
  gradient = 'primary',
  delay = 0,
  link = null,
}) {
  const navigate = useNavigate()

  const gradients = {
    primary: 'bg-gradient-to-br from-green-500 to-green-600',
    blue: 'bg-gradient-to-br from-blue-500 to-blue-600',
    purple: 'bg-gradient-to-br from-pink-500 to-pink-600',
    orange: 'bg-gradient-to-br from-orange-500 to-orange-600',
  }

  const bgGradients = {
    primary: 'bg-gradient-to-br from-green-500/10 to-green-600/5',
    blue: 'bg-gradient-to-br from-blue-500/10 to-blue-600/5',
    purple: 'bg-gradient-to-br from-pink-500/10 to-pink-600/5',
    orange: 'bg-gradient-to-br from-orange-500/10 to-orange-600/5',
  }

  const isPositive = trendValue && trendValue > 0

  const handleClick = () => {
    if (link) {
      navigate(link)
    }
  }

  // Format value to short form
  const formatValue = (val) => {
    if (typeof val === 'string' && val.startsWith('$')) {
      // Extract number from currency string
      const num = parseFloat(val.replace(/[$,]/g, ''))
      if (num >= 1000000) {
        return `$${(num / 1000000).toFixed(1)}M`
      }
      if (num >= 1000) {
        return `$${(num / 1000).toFixed(1)}K`
      }
      return val
    }
    
    if (typeof val === 'number') {
      return formatNumber(val)
    }
    
    return val
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay, duration: 0.3 }}
      whileHover={{ scale: link ? 1.02 : 1 }}
      onClick={handleClick}
      className={cn(
        'card p-6 transition-all duration-200 relative overflow-hidden',
        bgGradients[gradient],
        link && 'cursor-pointer hover:shadow-lg'
      )}
    >
      {/* Decorative gradient overlay */}
      <div className={cn(
        'absolute top-0 right-0 w-32 h-32 blur-3xl opacity-20 rounded-full',
        gradient === 'primary' && 'bg-green-500',
        gradient === 'blue' && 'bg-blue-500',
        gradient === 'purple' && 'bg-pink-500',
        gradient === 'orange' && 'bg-orange-500'
      )} />

      <div className="relative z-10">
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <p className="text-sm text-text-secondary mb-2">{title}</p>
            <h3 className="text-3xl md:text-4xl font-bold text-text-primary">
              {formatValue(value)}
            </h3>
          </div>
          <div className={cn(
            'w-12 h-12 rounded-xl flex items-center justify-center shadow-inner',
            gradients[gradient]
          )}>
            <Icon className="w-6 h-6 text-white" strokeWidth={2} />
          </div>
        </div>

        <div className="flex items-center justify-between">
          <p className="text-xs text-text-tertiary">{subtitle}</p>
          {trendValue !== undefined && (
            <div className={cn(
              'flex items-center gap-1 text-xs font-semibold px-2 py-1 rounded-full',
              isPositive 
                ? 'text-green-500 bg-green-500/10' 
                : 'text-red-500 bg-red-500/10'
            )}>
              {isPositive ? (
                <TrendingUp className="w-3.5 h-3.5" strokeWidth={2.5} />
              ) : (
                <TrendingDown className="w-3.5 h-3.5" strokeWidth={2.5} />
              )}
              <span>{isPositive ? '↑' : '↓'} {Math.abs(trendValue).toFixed(1)}%</span>
            </div>
          )}
        </div>
      </div>
    </motion.div>
  )
}
