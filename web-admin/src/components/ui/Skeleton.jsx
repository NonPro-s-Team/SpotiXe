import { motion } from 'framer-motion';
import { cn } from '@utils/helpers';

/**
 * Skeleton loading component with shimmer animation
 * 
 * @param {Object} props - Component props
 * @param {string} props.className - Additional CSS classes
 * @param {string} props.variant - Skeleton variant (text, circular, rectangular)
 * @param {number} props.width - Custom width
 * @param {number} props.height - Custom height
 * @param {number} props.count - Number of skeleton elements to render
 */
export default function Skeleton({
  className,
  variant = 'rectangular',
  width,
  height,
  count = 1,
  ...props
}) {
  const variantClasses = {
    text: 'h-4 w-full rounded',
    circular: 'rounded-full',
    rectangular: 'rounded-lg',
  };

  const skeletonElement = (
    <div
      className={cn(
        'animate-pulse bg-gradient-to-r from-gray-800 via-gray-700 to-gray-800 bg-[length:200%_100%]',
        variantClasses[variant],
        className
      )}
      style={{ width, height }}
      {...props}
    />
  );

  if (count === 1) {
    return skeletonElement;
  }

  return (
    <>
      {Array.from({ length: count }).map((_, index) => (
        <motion.div
          key={index}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: index * 0.05 }}
        >
          {skeletonElement}
        </motion.div>
      ))}
    </>
  );
}

/**
 * Table row skeleton for song list
 */
export function TableRowSkeleton({ columns = 9 }) {
  return (
    <tr className="border-b border-gray-700">
      <td className="px-4 py-4">
        <Skeleton variant="rectangular" width={16} height={16} />
      </td>
      <td className="px-4 py-4">
        <Skeleton variant="rectangular" width={50} height={50} />
      </td>
      {Array.from({ length: columns - 3 }).map((_, i) => (
        <td key={i} className="px-4 py-4">
          <Skeleton className="h-4 w-24" />
        </td>
      ))}
      <td className="px-4 py-4">
        <Skeleton variant="circular" width={32} height={32} />
      </td>
    </tr>
  );
}

/**
 * Card skeleton for mobile view
 */
export function CardSkeleton() {
  return (
    <div className="bg-gray-800 rounded-xl p-4 border border-gray-700">
      <div className="flex items-center gap-4 mb-4">
        <Skeleton variant="rectangular" width={60} height={60} />
        <div className="flex-1 space-y-2">
          <Skeleton className="h-5 w-3/4" />
          <Skeleton className="h-4 w-1/2" />
        </div>
      </div>
      <div className="space-y-2">
        <Skeleton className="h-3 w-full" />
        <Skeleton className="h-3 w-2/3" />
      </div>
    </div>
  );
}
