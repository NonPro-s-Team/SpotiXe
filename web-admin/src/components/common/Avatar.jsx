import { getInitials } from '@utils/helpers'
import { User } from 'lucide-react'
import { cn } from '@utils/helpers'
// import { useState } from 'react'

export default function Avatar({
  src,
  name,
  size = 'md',
  className,
  ...props
}) {
  // const [imageError, setImageError] = useState(false)
  
  const sizes = {
    sm: 'w-8 h-8 text-xs',
    md: 'w-10 h-10 text-sm',
    lg: 'w-12 h-12 text-base',
    xl: 'w-16 h-16 text-lg',
    '2xl': 'w-24 h-24 text-2xl',
  }

  // Debug log
  // if (src) {
  //   console.log('Avatar rendering with src:', src)
  // }

  if (src) {
    return (
      <img
        src={src}
        alt={name || 'Avatar'}
        // onError={(e) => {
        //   console.error('Image failed to load:', src, e)
        //   setImageError(true)
        // }}
        // onLoad={() => {
        //   console.log('Image loaded successfully:', src)
        // }}
        className={cn(
          'rounded-full object-cover border-2 border-border',
          sizes[size],
          className
        )}
        {...props}
      />
    )
  }

  if (name) {
    return (
      <div
        className={cn(
          'rounded-full bg-gradient-primary flex items-center justify-center font-semibold text-white',
          sizes[size],
          className
        )}
        {...props}
      >
        {getInitials(name)}
      </div>
    )
  }

  return (
    <div
      className={cn(
        'rounded-full bg-bg-hover flex items-center justify-center text-text-tertiary',
        sizes[size],
        className
      )}
      {...props}
    >
      <User className="w-1/2 h-1/2" />
    </div>
  )
}
