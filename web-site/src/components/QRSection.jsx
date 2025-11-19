import React, { useEffect, useRef, useState } from 'react';
import { motion } from 'framer-motion';
import { QrCode, Smartphone, Zap } from 'lucide-react';

const QRSection = () => {
  const [isVisible, setIsVisible] = useState(false);
  const sectionRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
        }
      },
      { threshold: 0.2 }
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

  return (
    <section
      ref={sectionRef}
      className="relative py-20 lg:py-32 bg-gradient-to-b from-spotify-black to-spotify-dark overflow-hidden"
      id="download"
      aria-label="Download Section"
    >
      {/* Background Decorations */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute top-1/2 left-1/4 w-64 h-64 bg-spotify-green/5 rounded-full blur-3xl" />
        <div className="absolute top-1/2 right-1/4 w-64 h-64 bg-accent-purple/5 rounded-full blur-3xl" />
      </div>

      <div className="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="max-w-4xl mx-auto">
          {/* Section Header */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={isVisible ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.8 }}
            className="text-center mb-12 lg:mb-16"
          >
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={isVisible ? { opacity: 1, scale: 1 } : {}}
              transition={{ duration: 0.6, delay: 0.2 }}
              className="inline-flex items-center space-x-2 px-4 py-2 bg-spotify-green/10 backdrop-blur-sm border border-spotify-green/20 rounded-full mb-6"
            >
              <Zap className="w-4 h-4 text-spotify-green" />
              <span className="text-sm font-medium text-spotify-green">Quick Access</span>
            </motion.div>

            <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-white mb-4">
              Get Started in{' '}
              <span className="bg-gradient-to-r from-spotify-green to-accent-blue bg-clip-text text-transparent">
                Seconds
              </span>
            </h2>
            <p className="text-lg lg:text-xl text-spotify-gray max-w-2xl mx-auto">
              Scan the QR code with your phone camera to download SpotiXe instantly
            </p>
          </motion.div>

          {/* QR Code Container */}
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isVisible ? { opacity: 1, scale: 1 } : {}}
            transition={{ duration: 0.8, delay: 0.4 }}
            className="flex flex-col items-center"
          >
            {/* QR Code Card */}
            <motion.div
              whileHover={{ scale: 1.05, rotate: 2 }}
              transition={{ duration: 0.3 }}
              className="relative group"
            >
              {/* Animated Glow */}
              <div className="absolute -inset-4 bg-gradient-to-r from-spotify-green via-accent-purple to-spotify-green bg-[length:200%_100%] rounded-3xl blur-xl opacity-50 group-hover:opacity-75 transition-opacity duration-500 animate-pulse-glow" />
              
              {/* QR Code */}
              <div className="relative bg-white p-8 sm:p-10 lg:p-12 rounded-2xl shadow-2xl">
                {/* Actual QR Code */}
                <div className="w-48 h-48 sm:w-56 sm:h-56 lg:w-64 lg:h-64 flex items-center justify-center">
                  <img 
                    src="/qr-code-dynamic.svg" 
                    alt="QR Code to download SpotiXe app"
                    className="w-full h-full object-contain"
                  />
                </div>

                {/* Corner Decorations */}
                <div className="absolute top-4 left-4 w-6 h-6 border-t-4 border-l-4 border-spotify-green rounded-tl-lg" />
                <div className="absolute top-4 right-4 w-6 h-6 border-t-4 border-r-4 border-spotify-green rounded-tr-lg" />
                <div className="absolute bottom-4 left-4 w-6 h-6 border-b-4 border-l-4 border-spotify-green rounded-bl-lg" />
                <div className="absolute bottom-4 right-4 w-6 h-6 border-b-4 border-r-4 border-spotify-green rounded-br-lg" />
              </div>
            </motion.div>

            {/* Instructions */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={isVisible ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: 0.6 }}
              className="mt-8 lg:mt-12 text-center"
            >
              <h3 className="text-xl lg:text-2xl font-bold text-white mb-3">
                Scan to Download SpotiXe App
              </h3>
              <p className="text-spotify-gray mb-8 max-w-md mx-auto">
                Available for iOS and Android. Start your free trial today and unlock unlimited music.
              </p>

              {/* Platform Badges */}
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                <motion.a
                  href="#"
                  whileHover={{ scale: 1.05, y: -2 }}
                  whileTap={{ scale: 0.95 }}
                  className="group relative overflow-hidden px-8 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl hover:bg-white/20 transition-all duration-300"
                  aria-label="Download on App Store"
                >
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 bg-white/10 rounded-lg flex items-center justify-center">
                      <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M18.71 19.5C17.88 20.74 17 21.95 15.66 21.97C14.32 22 13.89 21.18 12.37 21.18C10.84 21.18 10.37 21.95 9.1 22C7.79 22.05 6.8 20.68 5.96 19.47C4.25 17 2.94 12.45 4.7 9.39C5.57 7.87 7.13 6.91 8.82 6.88C10.1 6.86 11.32 7.75 12.11 7.75C12.89 7.75 14.37 6.68 15.92 6.84C16.57 6.87 18.39 7.1 19.56 8.82C19.47 8.88 17.39 10.1 17.41 12.63C17.44 15.65 20.06 16.66 20.09 16.67C20.06 16.74 19.67 18.11 18.71 19.5M13 3.5C13.73 2.67 14.94 2.04 15.94 2C16.07 3.17 15.6 4.35 14.9 5.19C14.21 6.04 13.07 6.7 11.95 6.61C11.8 5.46 12.36 4.26 13 3.5Z"/>
                      </svg>
                    </div>
                    <div className="text-left">
                      <div className="text-xs text-spotify-gray">Download on the</div>
                      <div className="text-sm font-semibold text-white">App Store</div>
                    </div>
                  </div>
                </motion.a>

                <motion.a
                  href="#"
                  whileHover={{ scale: 1.05, y: -2 }}
                  whileTap={{ scale: 0.95 }}
                  className="group relative overflow-hidden px-8 py-3 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl hover:bg-white/20 transition-all duration-300"
                  aria-label="Get it on Google Play"
                >
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 bg-white/10 rounded-lg flex items-center justify-center">
                      <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3,20.5V3.5C3,2.91 3.34,2.39 3.84,2.15L13.69,12L3.84,21.85C3.34,21.6 3,21.09 3,20.5M16.81,15.12L6.05,21.34L14.54,12.85L16.81,15.12M20.16,10.81C20.5,11.08 20.75,11.5 20.75,12C20.75,12.5 20.5,12.92 20.16,13.19L17.89,14.5L15.39,12L17.89,9.5L20.16,10.81M6.05,2.66L16.81,8.88L14.54,11.15L6.05,2.66Z"/>
                      </svg>
                    </div>
                    <div className="text-left">
                      <div className="text-xs text-spotify-gray">GET IT ON</div>
                      <div className="text-sm font-semibold text-white">Google Play</div>
                    </div>
                  </div>
                </motion.a>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default QRSection;
