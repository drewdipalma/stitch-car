import React from 'react';

export default (rotation: number) => (
  <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" width="25" height="26" viewBox="0 0 25 26">
    <defs>
      <polygon id="fast_icon-b" points="11.124 .502 22.048 11.428 18.598 14.877 13.501 10.278 13.501 15.494 8.507 15.494 8.507 10.296 3.648 14.877 .198 11.428"/>
      <filter id="fast_icon-a" width="132%" height="146.7%" x="-16%" y="-23.3%" filterUnits="objectBoundingBox">
        <feMorphology in="SourceAlpha" operator="dilate" radius=".5" result="shadowSpreadOuter1"/>
        <feOffset in="shadowSpreadOuter1" result="shadowOffsetOuter1"/>
        <feGaussianBlur in="shadowOffsetOuter1" result="shadowBlurOuter1" stdDeviation="1"/>
        <feComposite in="shadowBlurOuter1" in2="SourceAlpha" operator="out" result="shadowBlurOuter1"/>
        <feColorMatrix in="shadowBlurOuter1" values="0 0 0 0 0.545098039   0 0 0 0 0.545098039   0 0 0 0 0.545098039  0 0 0 0.5 0"/>
      </filter>
      <rect id="fast_icon-d" width="5" height="2" x="8.5" y="17.5"/>
      <filter id="fast_icon-c" width="240%" height="450%" x="-70%" y="-175%" filterUnits="objectBoundingBox">
        <feMorphology in="SourceAlpha" operator="dilate" radius=".5" result="shadowSpreadOuter1"/>
        <feOffset in="shadowSpreadOuter1" result="shadowOffsetOuter1"/>
        <feGaussianBlur in="shadowOffsetOuter1" result="shadowBlurOuter1" stdDeviation="1"/>
        <feComposite in="shadowBlurOuter1" in2="SourceAlpha" operator="out" result="shadowBlurOuter1"/>
        <feColorMatrix in="shadowBlurOuter1" values="0 0 0 0 0.545098039   0 0 0 0 0.545098039   0 0 0 0 0.545098039  0 0 0 0.5 0"/>
      </filter>
      <rect id="fast_icon-f" width="5" height="2" x="8.5" y="21.5" rx=".5"/>
      <filter id="fast_icon-e" width="240%" height="450%" x="-70%" y="-175%" filterUnits="objectBoundingBox">
        <feMorphology in="SourceAlpha" operator="dilate" radius=".5" result="shadowSpreadOuter1"/>
        <feOffset in="shadowSpreadOuter1" result="shadowOffsetOuter1"/>
        <feGaussianBlur in="shadowOffsetOuter1" result="shadowBlurOuter1" stdDeviation="1"/>
        <feComposite in="shadowBlurOuter1" in2="SourceAlpha" operator="out" result="shadowBlurOuter1"/>
        <feColorMatrix in="shadowBlurOuter1" values="0 0 0 0 0.545098039   0 0 0 0 0.545098039   0 0 0 0 0.545098039  0 0 0 0.5 0"/>
      </filter>
    </defs>
    <g fill="none" fillRule="evenodd" strokeLinejoin="round" transform={`rotate(${rotation}, 12.5, 13)`}>
      <use fill="#000" filter="url(#fast_icon-a)" xlinkHref="#fast_icon-b"/>
      <use fill="#FFF" stroke="#464C4F" xlinkHref="#fast_icon-b"/>
      <use fill="#000" filter="url(#fast_icon-c)" xlinkHref="#fast_icon-d"/>
      <use fill="#FFF" stroke="#464C4F" xlinkHref="#fast_icon-d"/>
      <g>
        <use fill="#000" filter="url(#fast_icon-e)" xlinkHref="#fast_icon-f"/>
        <use fill="#FFF" stroke="#464C4F" xlinkHref="#fast_icon-f"/>
      </g>
    </g>
  </svg>
);
