import { useState, useEffect, useCallback } from 'react';
import { useUpdateProgress } from '@/queries/enrollments/useUpdateProgress';
import { useEnrollment } from '@/queries';
import { useQueryClient } from '@tanstack/react-query';

export const useTimer = (enrollmentId: number) => {
    const [isRunning, setIsRunning] = useState(false);
    const [totalSeconds, setTotalSeconds] = useState(0);
    const [incrementalSeconds, setIncrementalSeconds] = useState(0);
    const queryClient = useQueryClient();

    const { data: enrollment } = useEnrollment(enrollmentId);
    const { mutate: updateTime } = useUpdateProgress();

    // Initialize time from enrollment data
    useEffect(() => {
        if (enrollment?.data?.timeSpent) {
            // Convert minutes from backend to seconds for our state
            setTotalSeconds(enrollment.data.timeSpent * 60);
            setIncrementalSeconds(0);
        }
    }, [enrollment]);

    useEffect(() => {
        let interval: NodeJS.Timeout;
        if (isRunning) {
            interval = setInterval(() => {
                setTotalSeconds(prev => prev + 1);
                setIncrementalSeconds(prev => prev + 1);
            }, 1000);
        }
        return () => {
            if (interval) {
                clearInterval(interval);
            }
        };
    }, [isRunning]);

    const startTimer = useCallback(() => {
        setIsRunning(true);
    }, []);

    const stopTimer = useCallback(() => {
        setIsRunning(false);
        if (incrementalSeconds > 0) {
            // Convert seconds to minutes (rounded) when sending to backend
            const minutesSpent = Math.round(incrementalSeconds / 60);
            if (minutesSpent > 0) { // Only update if we have at least a minute
                updateTime({
                    enrollmentId,
                    timeSpent: minutesSpent
                }, {
                    onSuccess: () => {
                        queryClient.invalidateQueries({
                            queryKey: ['enrollment', enrollmentId]
                        });
                        setIncrementalSeconds(0);
                    }
                });
            }
        }
    }, [updateTime, enrollmentId, incrementalSeconds, queryClient]);

    const resetTimer = useCallback(() => {
        setTotalSeconds(0);
        setIncrementalSeconds(0);
        setIsRunning(false);
    }, []);

    return {
        totalSeconds,
        isRunning,
        startTimer,
        stopTimer,
        resetTimer
    };
};