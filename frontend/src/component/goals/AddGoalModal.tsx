import React, { useState } from 'react';
import { useCreateGoal } from '@/queries/goals/useCreateGoal';
import { useGoals } from '@/queries/goals/useGoals';
import { Enrollment } from '@/types/databaseTypes';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';
import axiosInstance from '@/component/axiosInstance';

interface AddGoalModalProps {
    isOpen: boolean;
    onClose: () => void;
    enrollments: Enrollment[];
}

export default function AddGoalModal({ isOpen, onClose, enrollments }: AddGoalModalProps) {
    const [description, setDescription] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [reward, setReward] = useState('');
    const [selectedEnrollments, setSelectedEnrollments] = useState<number[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    
    const createGoalMutation = useCreateGoal();
    const { refetch: refetchGoals } = useGoals();
    const queryClient = useQueryClient();
    
    const resetForm = () => {
        setDescription('');
        setStartDate('');
        setEndDate('');
        setReward('');
        setSelectedEnrollments([]);
    };
    
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        
        try {
            // Create the goal
            await createGoalMutation.mutateAsync({
                description,
                startDate,
                endDate,
                reward
            });
            
            // Refetch goals to get the newly created goal ID
            await queryClient.invalidateQueries({ queryKey: ['goals'] });
            const { data: goalsResponse } = await refetchGoals();
            
            // Find the newly created goal
            const newGoal = Array.isArray(goalsResponse?.data) ? 
                goalsResponse?.data.find(g => 
                    g.description === description && 
                    g.startDate.split('T')[0] === startDate && 
                    g.endDate.split('T')[0] === endDate
                ) : null;
            
            // If courses are selected and we found the new goal, add them to the goal
            if (selectedEnrollments.length > 0 && newGoal?.id) {
                try {
                    // Direct API call instead of using a hook inside a handler
                    await axiosInstance.post(`/goals/${newGoal.id}`, { 
                        enrollmentIds: selectedEnrollments 
                    });
                    // Refetch goals again to get updated data
                    queryClient.invalidateQueries({ queryKey: ['goals'] });
                } catch (enrollmentError) {
                    console.error('Error adding enrollments:', enrollmentError);
                    toast.error('Goal created but failed to add courses');
                }
            }
            
            toast.success('Goal created successfully');
            resetForm();
            onClose();
        } catch (error) {
            console.error('Error creating goal:', error);
            toast.error('Failed to create goal');
        } finally {
            setIsSubmitting(false);
        }
    };
    
    const handleEnrollmentToggle = (enrollmentId: number) => {
        setSelectedEnrollments((prev) => {
            if (prev.includes(enrollmentId)) {
                return prev.filter(id => id !== enrollmentId);
            } else {
                return [...prev, enrollmentId];
            }
        });
    };
    
    if (!isOpen) return null;
    
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 w-full max-w-lg">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-xl font-bold">Create New Goal</h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
                        &times;
                    </button>
                </div>
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Description
                        </label>
                        <input
                            type="text"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            className="w-full px-3 py-2 border rounded-md"
                            required
                            placeholder="Complete React courses"
                        />
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4 mb-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Start Date
                            </label>
                            <input
                                type="date"
                                value={startDate}
                                onChange={(e) => setStartDate(e.target.value)}
                                className="w-full px-3 py-2 border rounded-md"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                End Date
                            </label>
                            <input
                                type="date"
                                value={endDate}
                                onChange={(e) => setEndDate(e.target.value)}
                                className="w-full px-3 py-2 border rounded-md"
                                required
                                min={startDate} // Prevent end date before start date
                            />
                        </div>
                    </div>
                    
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Reward (what will you give yourself?)
                        </label>
                        <input
                            type="text"
                            value={reward}
                            onChange={(e) => setReward(e.target.value)}
                            className="w-full px-3 py-2 border rounded-md"
                            required
                            placeholder="Weekend trip to the beach"
                        />
                    </div>
                    
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Select Courses for this Goal
                        </label>
                        <div className="max-h-40 overflow-y-auto border rounded-md p-2">
                            {enrollments.length === 0 ? (
                                <p className="text-sm text-gray-500">No enrolled courses available.</p>
                            ) : (
                                enrollments.map((enrollment) => (
                                    <div key={enrollment.id} className="flex items-center mb-1">
                                        <input
                                            type="checkbox"
                                            id={`enrollment-${enrollment.id}`}
                                            checked={selectedEnrollments.includes(enrollment.id)}
                                            onChange={() => handleEnrollmentToggle(enrollment.id)}
                                            className="mr-2"
                                        />
                                        <label 
                                            htmlFor={`enrollment-${enrollment.id}`}
                                            className="text-sm flex-1 cursor-pointer"
                                        >
                                            {enrollment.course.title}
                                        </label>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                    
                    <div className="flex justify-end space-x-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                            disabled={isSubmitting || createGoalMutation.isPending}
                        >
                            {isSubmitting || createGoalMutation.isPending ? 'Creating...' : 'Create Goal'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}