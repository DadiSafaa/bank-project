import React, { useState, useEffect } from 'react';

const TextAnimation = ({ text, speed = 150 }) => {
    const [displayedText, setDisplayedText] = useState('');
    const [index, setIndex] = useState(0);

    useEffect(() => {
        if (index < text.length) {
            const timer = setTimeout(() => {
                setDisplayedText(displayedText + text[index]);
                setIndex(index + 1);
            }, speed);

            return () => clearTimeout(timer);
        } else {
            // Réinitialiser pour redémarrer l'animation
            setTimeout(() => {
                setDisplayedText('');
                setIndex(0);
            }, 2000); // Attendez un peu avant de redémarrer
        }
    }, [index, text, speed, displayedText]);


    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <div className="text-animation" style={{ fontSize: '24px', fontWeight: 'bold' }}>
                {displayedText}
            </div>
        </div>
    );

    }
export default TextAnimation;
