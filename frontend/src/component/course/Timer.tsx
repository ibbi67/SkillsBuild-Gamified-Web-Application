import { useTimer } from "@/component/course/useTimer";
import { PlayCircle, StopCircle } from "lucide-react";

interface TimerProps {
    enrollmentId: number;
}

export const Timer = ({ enrollmentId }: TimerProps) => {
    const { totalSeconds, isRunning, startTimer, stopTimer } = useTimer(enrollmentId);

    const formatTime = (seconds: number): string => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const remainingSeconds = seconds % 60;

        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    return (
        <div className="flex items-center gap-3 bg-gray-50 rounded-lg px-4 py-2 shadow-sm">
            <div className="flex items-center gap-1">
                <span className="text-gray-500 text-xs">Time Spent:</span>
                <span className="font-mono text-lg font-semibold text-gray-700">{formatTime(totalSeconds)}</span>
            </div>
            <button 
                onClick={isRunning ? stopTimer : startTimer}
                className={`rounded-full p-1 transition-colors duration-200 ${
                    isRunning 
                        ? 'bg-red-100 text-red-600 hover:bg-red-200' 
                        : 'bg-green-100 text-green-600 hover:bg-green-200'
                }`}
                title={isRunning ? 'Stop Timer' : 'Start Timer'}
            >
                {isRunning ? <StopCircle size={24} /> : <PlayCircle size={24} />}
            </button>
        </div>
    );
};