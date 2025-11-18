import { motion } from 'framer-motion'

export default function LoadingScreen() {
  return (
    <div className="fixed inset-0 bg-bg-primary flex items-center justify-center">
      <div className="text-center">
        <motion.div
          animate={{
            scale: [1, 1.2, 1],
            rotate: [0, 180, 360],
          }}
          transition={{
            duration: 2,
            repeat: Infinity,
            ease: "easeInOut",
          }}
          className="w-16 h-16 mx-auto mb-4"
        >
          <div className="w-full h-full rounded-full bg-gradient-primary"></div>
        </motion.div>
        <h2 className="text-xl font-semibold text-gradient">Loading SpotiXe...</h2>
      </div>
    </div>
  )
}

export function LoadingSkeleton({ className, ...props }) {
  return (
    <div className={`skeleton ${className}`} {...props} />
  )
}

export function TableSkeleton({ rows = 5, columns = 5 }) {
  return (
    <div className="space-y-3">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="flex gap-4">
          {Array.from({ length: columns }).map((_, j) => (
            <LoadingSkeleton key={j} className="h-12 flex-1" />
          ))}
        </div>
      ))}
    </div>
  )
}

export function CardSkeleton() {
  return (
    <div className="card p-6">
      <LoadingSkeleton className="h-6 w-1/3 mb-4" />
      <LoadingSkeleton className="h-20 w-full mb-3" />
      <LoadingSkeleton className="h-4 w-2/3" />
    </div>
  )
}
