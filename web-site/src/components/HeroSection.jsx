import React, { useEffect, useRef, useState } from 'react';
import { motion, useScroll, useTransform } from 'framer-motion';
import { Play, Download, Sparkles, ChevronDown } from 'lucide-react';

const HeroSection = () => {
  const [isVisible, setIsVisible] = useState(false);
  const [isScrolled, setIsScrolled] = useState(false);
  const sectionRef = useRef(null);
  const containerRef = useRef(null);
  
  const { scrollYProgress } = useScroll({
    target: sectionRef,
    offset: ["start start", "end start"]
  });

  const y = useTransform(scrollYProgress, [0, 1], [0, 100]);
  const opacity = useTransform(scrollYProgress, [0, 0.3], [1, 0.3]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
        }
      },
      { threshold: 0.1 }
    );

    if (sectionRef.current) {
      observer.observe(sectionRef.current);
    }

    return () => {
      if (sectionRef.current) {
        observer.unobserve(sectionRef.current);
      }
    };
  }, []);

  const handleScrollDown = () => {
    const nextSection = sectionRef.current?.nextElementSibling;
    if (nextSection) {
      nextSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2,
        delayChildren: 0.3,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.6, ease: "easeOut" },
    },
  };

  return (
    <section
      ref={sectionRef}
      className="relative min-h-screen w-full flex items-center justify-center overflow-hidden pt-20 pb-20 lg:pt-24 lg:pb-0"
      style={{
        background: 'linear-gradient(135deg, #000000 0%, #0a3a2a 50%, #0f0a1a 100%)'
      }}
      aria-label="Hero Section"
    >
      {/* Animated Background Orbs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {/* Green orb - top left */}
        <motion.div
          className="absolute -top-1/3 -left-1/4 w-96 h-96 rounded-full blur-3xl opacity-20"
          style={{
            background: 'radial-gradient(circle, rgba(29, 185, 84, 0.3) 0%, transparent 70%)',
          }}
          animate={{
            x: [0, 50, 0],
            y: [0, -50, 0],
          }}
          transition={{
            duration: 20,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        />

        {/* Purple orb - bottom right */}
        <motion.div
          className="absolute -bottom-1/3 -right-1/4 w-96 h-96 rounded-full blur-3xl opacity-20"
          style={{
            background: 'radial-gradient(circle, rgba(167, 139, 250, 0.3) 0%, transparent 70%)',
          }}
          animate={{
            x: [0, -50, 0],
            y: [0, 50, 0],
          }}
          transition={{
            duration: 25,
            repeat: Infinity,
            ease: "easeInOut",
            delay: 0.5,
          }}
        />

        {/* Blue orb - center top */}
        <motion.div
          className="absolute top-1/4 right-1/3 w-80 h-80 rounded-full blur-3xl opacity-15"
          style={{
            background: 'radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%)',
          }}
          animate={{
            scale: [1, 1.1, 1],
            opacity: [0.15, 0.25, 0.15],
          }}
          transition={{
            duration: 8,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        />
      </div>

      {/* Main Content */}
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10 w-full">
        <motion.div
          ref={containerRef}
          className="flex flex-col items-center justify-center text-center max-w-5xl mx-auto"
          variants={containerVariants}
          initial="hidden"
          animate={isVisible ? "visible" : "hidden"}
        >
          {/* Content */}
          <motion.div className="flex flex-col justify-center items-center text-center">
            {/* Badge */}
            <motion.div
              variants={itemVariants}
              className="inline-flex items-center space-x-2 px-4 py-2 mb-6 lg:mb-8 rounded-full border"
              style={{
                background: 'rgba(29, 185, 84, 0.1)',
                borderColor: 'rgba(29, 185, 84, 0.3)',
              }}
            >
              <Sparkles className="w-4 h-4 text-spotify-green flex-shrink-0" />
              <span className="text-sm font-medium text-white whitespace-nowrap">✨ Now Available Worldwide</span>
            </motion.div>

            {/* Main Heading */}
            <motion.h1
              variants={itemVariants}
              className="text-4xl sm:text-5xl lg:text-6xl xl:text-7xl font-bold text-white mb-6 leading-tight tracking-tight"
            >
              Discover a World of{' '}
              <span className="bg-gradient-to-r from-spotify-green via-accent-green to-accent-blue bg-clip-text text-transparent animate-pulse">
                Music
              </span>
              {' '}with SpotiXe
            </motion.h1>

            {/* Subtitle */}
            <motion.p
              variants={itemVariants}
              className="text-lg sm:text-xl text-spotify-gray mb-8 lg:mb-10 max-w-2xl leading-relaxed"
            >
              Stream millions of songs, discover new artists, and enjoy unlimited music with crystal-clear sound quality. Your soundtrack to life starts here.
            </motion.p>

            {/* CTA Buttons */}
            <motion.div
              variants={itemVariants}
              className="flex flex-col sm:flex-row gap-4 w-full sm:w-auto justify-center lg:justify-start mb-12 lg:mb-16"
            >
              {/* Primary Button */}
              <motion.a
                href="#download"
                whileHover={{
                  scale: 1.05,
                  boxShadow: '0 0 40px rgba(29, 185, 84, 0.6), 0 0 80px rgba(29, 185, 84, 0.3)',
                }}
                whileTap={{ scale: 0.95 }}
                className="group relative px-8 py-4 text-white font-bold rounded-full overflow-hidden transition-all duration-300 flex items-center justify-center space-x-2 w-full sm:w-auto"
                style={{
                  background: 'linear-gradient(135deg, #1DB954 0%, #1ed760 100%)',
                }}
              >
                <Download className="w-5 h-5" />
                <span>Download Now</span>
                <div className="absolute inset-0 bg-white/20 transform scale-x-0 group-hover:scale-x-100 transition-transform origin-left duration-300 pointer-events-none" />
              </motion.a>

              {/* Secondary Button */}
              <motion.a
                href="#demo"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="group px-8 py-4 text-white font-semibold rounded-full flex items-center justify-center space-x-2 w-full sm:w-auto transition-all duration-300"
                style={{
                  background: 'rgba(255, 255, 255, 0.05)',
                  border: '1px solid rgba(255, 255, 255, 0.2)',
                }}
              >
                <Play className="w-5 h-5 group-hover:text-spotify-green transition-colors duration-300" />
                <span>Watch Demo</span>
              </motion.a>
            </motion.div>

            {/* Stats */}
            <motion.div
              variants={itemVariants}
              className="grid grid-cols-3 gap-6 sm:gap-10 w-full max-w-2xl mx-auto"
            >
              {[
                { value: '100M+', label: 'Songs' },
                { value: '500K+', label: 'Artists' },
                { value: '50M+', label: 'Users' },
              ].map((stat, index) => (
                <div key={index} className="text-center">
                  <div className="text-2xl sm:text-3xl lg:text-4xl font-bold text-white mb-1 tracking-tight">
                    {stat.value}
                  </div>
                  <div className="text-xs sm:text-sm text-spotify-gray font-medium">
                    {stat.label}
                  </div>
                </div>
              ))}
            </motion.div>
          </motion.div>
        </motion.div>
      </div>

      {/* Scroll Down Indicator */}
      <motion.div
        className="absolute bottom-8 left-1/2 transform -translate-x-1/2 flex flex-col items-center"
        initial={{ opacity: 0 }}
        animate={isVisible ? { opacity: 1 } : {}}
        transition={{ delay: 2, duration: 1 }}
      >
        <span className="text-xs text-white/50 mb-3 font-medium tracking-widest">SCROLL</span>
        <motion.button
          onClick={handleScrollDown}
          className="p-2 rounded-full hover:bg-white/10 transition-colors duration-300"
          aria-label="Scroll to next section"
          animate={{ y: [0, 8, 0] }}
          transition={{ duration: 2, repeat: Infinity, ease: "easeInOut" }}
        >
          <ChevronDown className="w-6 h-6 text-spotify-green opacity-70" />
        </motion.button>
      </motion.div>
    </section>
  );
};

export default HeroSection;
