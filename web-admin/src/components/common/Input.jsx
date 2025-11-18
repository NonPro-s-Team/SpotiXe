import { forwardRef } from 'react'
import { cn } from '@utils/helpers'

const Input = forwardRef(function Input({
  label,
  error,
  icon: Icon,
  className,
  containerClassName,
  ...props
}, ref) {
  return (
    <div className={cn('w-full', containerClassName)}>
      {label && (
        <label className="block text-sm font-medium text-text-secondary mb-2">
          {label}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-text-tertiary">
            <Icon className="h-5 w-5" />
          </div>
        )}
        <input
          ref={ref}
          className={cn(
            'input-field',
            Icon && 'pl-10',
            error && 'border-apple-red focus:border-apple-red focus:ring-apple-red/20',
            className
          )}
          {...props}
        />
      </div>
      {error && (
        <p className="mt-1 text-sm text-apple-red">{error}</p>
      )}
    </div>
  )
})

export default Input

export const Textarea = forwardRef(function Textarea({
  label,
  error,
  className,
  containerClassName,
  ...props
}, ref) {
  return (
    <div className={cn('w-full', containerClassName)}>
      {label && (
        <label className="block text-sm font-medium text-text-secondary mb-2">
          {label}
        </label>
      )}
      <textarea
        ref={ref}
        className={cn(
          'input-field min-h-[100px] resize-y',
          error && 'border-apple-red focus:border-apple-red focus:ring-apple-red/20',
          className
        )}
        {...props}
      />
      {error && (
        <p className="mt-1 text-sm text-apple-red">{error}</p>
      )}
    </div>
  )
})

export const Select = forwardRef(function Select({
  label,
  error,
  options = [],
  className,
  containerClassName,
  ...props
}, ref) {
  return (
    <div className={cn('w-full', containerClassName)}>
      {label && (
        <label className="block text-sm font-medium text-text-secondary mb-2">
          {label}
        </label>
      )}
      <select
        ref={ref}
        className={cn(
          'input-field',
          error && 'border-apple-red focus:border-apple-red focus:ring-apple-red/20',
          className
        )}
        {...props}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && (
        <p className="mt-1 text-sm text-apple-red">{error}</p>
      )}
    </div>
  )
})
