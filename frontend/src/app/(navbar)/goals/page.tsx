"use client";

import React, { useState } from 'react';
import { useGoals } from '@/queries/goals/useGoals';
import { useDeleteGoal } from '@/queries/goals/useDeleteGoal';
import { useUpdateEnrollmentCompletion } from '@/queries/goals/useUpdateEnrollmentCompletion';
import { useEnrollments } from '@/queries';
import AddGoalModal from '@/component/goals/AddGoalModal';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';

export default function GoalsPage() {
    const [isAddGoalModalOpen, setIsAddGoalModalOpen] = useState(false);
    const [activeTab, setActiveTab] = useState<'active' | 'upcoming' | 'completed' | 'all'>('active');
    const { data: goals, isLoading, isError } = useGoals();
    const { data: enrollments } = useEnrollments();
    const deleteGoalMutation = useDeleteGoal();
    const updateEnrollmentCompletionMutation = useUpdateEnrollmentCompletion();
    const queryClient = useQueryClient();

    const handleDeleteGoal = async (goalId: number) => {
        try {
            await deleteGoalMutation.mutateAsync(goalId);
            toast.success('Goal deleted successfully');
            queryClient.invalidateQueries({ queryKey: ['goals'] });
        } catch (error) {
            toast.error('Failed to delete goal');
        }
    };

    const handleMarkCourseAsCompleted = async (goalId: number, enrollmentId: number) => {
        try {
            await updateEnrollmentCompletionMutation.mutateAsync({ goalId, enrollmentId });
            toast.success('Course marked as completed');
            queryClient.invalidateQueries({ queryKey: ['goals'] });
        } catch (error) {
            toast.error('Failed to update course completion status');
        }
    };

    const openAddGoalModal = () => {
        setIsAddGoalModalOpen(true);
    };

    const closeAddGoalModal = () => {
        setIsAddGoalModalOpen(false);
    };

    // Format date to 'MMM dd, yyyy'
    const formatDate = (date: Date) => {
        return date.toLocaleDateString('en-US', {
            month: 'short',
            day: '2-digit',
            year: 'numeric',
        });
    };

    // Filter goals based on active tab
    const filteredGoals = goals?.data.filter(goal => {
        const now = new Date();
        const startDate = new Date(goal.startDate);
        const endDate = new Date(goal.endDate);
        const isCompleted = goal.enrollments.length > 0 && 
            goal.enrollments.every(enrollment => enrollment.completed);
        
        switch(activeTab) {
            case 'active':
                return (startDate <= now && endDate >= now) && !isCompleted;
            case 'upcoming':
                return startDate > now;
            case 'completed':
                return isCompleted || endDate < now;
            case 'all':
            default:
                return true;
        }
    });

    // Check if a goal is overdue but not completed
    const isOverdue = (goal: any) => {
        const now = new Date();
        const endDate = new Date(goal.endDate);
        const isCompleted = goal.enrollments.length > 0 && 
            goal.enrollments.every(enrollment => enrollment.completed);
        
        return endDate < now && !isCompleted;
    };

    if (isLoading) {
        return <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-8 shadow-lg">Loading goals...</div>;
    }

    if (isError) {
        return <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-8 shadow-lg">Error loading goals</div>;
    }

    return (
        <div className="m-4 mx-auto w-4/5 rounded-lg bg-white p-8 shadow-lg">
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold">Your Learning Goals</h1>
                <button
                    onClick={openAddGoalModal}
                    className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                >
                    Add New Goal
                </button>
            </div>

            {/* Tabs */}
            <div className="flex space-x-2 mb-6 border-b">
                <button
                    className={`px-4 py-2 ${activeTab === 'active' ? 'border-b-2 border-blue-500 font-medium' : 'text-gray-500'}`}
                    onClick={() => setActiveTab('active')}
                >
                    Active
                </button>
                <button
                    className={`px-4 py-2 ${activeTab === 'upcoming' ? 'border-b-2 border-blue-500 font-medium' : 'text-gray-500'}`}
                    onClick={() => setActiveTab('upcoming')}
                >
                    Upcoming
                </button>
                <button
                    className={`px-4 py-2 ${activeTab === 'completed' ? 'border-b-2 border-blue-500 font-medium' : 'text-gray-500'}`}
                    onClick={() => setActiveTab('completed')}
                >
                    Completed/Past
                </button>
                <button
                    className={`px-4 py-2 ${activeTab === 'all' ? 'border-b-2 border-blue-500 font-medium' : 'text-gray-500'}`}
                    onClick={() => setActiveTab('all')}
                >
                    All Goals
                </button>
            </div>

            {filteredGoals?.length === 0 ? (
                <div className="text-center py-10">
                    <p className="text-gray-500">No goals found in this category.</p>
                    <button
                        onClick={openAddGoalModal}
                        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                    >
                        Create Your First Goal
                    </button>
                </div>
            ) : (
                <div className="grid md:grid-cols-2 gap-6">
                    {filteredGoals?.map((goal) => (
                        <div 
                            key={goal.id} 
                            className={`border rounded-lg p-6 ${
                                isOverdue(goal) 
                                    ? 'border-red-300 bg-red-50' 
                                    : 'border-gray-200'
                            }`}
                        >
                            <div className="flex justify-between items-start mb-4">
                                <div>
                                    <h3 className="text-lg font-semibold">{goal.description}</h3>
                                    <p className="text-sm text-gray-500">
                                        {formatDate(new Date(goal.startDate))} - {formatDate(new Date(goal.endDate))}
                                    </p>
                                    {isOverdue(goal) && (
                                        <span className="inline-block mt-1 px-2 py-1 bg-red-100 text-red-800 text-xs rounded">
                                            Overdue
                                        </span>
                                    )}
                                </div>
                                <button
                                    onClick={() => handleDeleteGoal(goal.id)}
                                    className="p-1 text-red-500 hover:text-red-700 transition"
                                >
                                    Delete
                                </button>
                            </div>

                            <p className="text-sm mb-4"><span className="font-medium">Reward:</span> {goal.reward}</p>

                            <div className="mt-4">
                                <h4 className="text-sm font-medium mb-2">Progress:</h4>
                                <div className="flex items-center mb-4">
                                    <div className="bg-gray-200 rounded-full h-2 flex-grow mr-2">
                                        <div
                                            className={`h-2 rounded-full ${
                                                isOverdue(goal) ? 'bg-red-500' : 'bg-green-500'
                                            }`}
                                            style={{
                                                width: `${
                                                    goal.enrollments.length > 0
                                                        ? (goal.enrollments.filter(e => e.completed).length / goal.enrollments.length) * 100
                                                        : 0
                                                }%`
                                            }}
                                        ></div>
                                    </div>
                                    <span className="text-sm">
                                        {goal.enrollments.filter(e => e.completed).length}/{goal.enrollments.length}
                                    </span>
                                </div>
                            </div>

                            <div>
                                <h4 className="text-sm font-medium mb-2">Courses in this goal:</h4>
                                {goal.enrollments.length === 0 ? (
                                    <p className="text-sm text-gray-500">No courses added to this goal.</p>
                                ) : (
                                    <div className="space-y-2">
                                        {goal.enrollments.map((enrollment) => (
                                            <div key={enrollment.id} className="flex items-center justify-between bg-gray-50 p-3 rounded">
                                                <span className="text-sm">{enrollment.course.title}</span>
                                                <button
                                                    onClick={() => handleMarkCourseAsCompleted(goal.id, enrollment.id)}
                                                    className={`px-3 py-1 text-xs rounded ${
                                                        enrollment.completed
                                                            ? 'bg-green-100 text-green-800 cursor-default'
                                                            : 'bg-blue-500 text-white hover:bg-blue-600'
                                                    }`}
                                                    disabled={enrollment.completed}
                                                >
                                                    {enrollment.completed ? 'Completed' : 'Mark Complete'}
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <AddGoalModal
                isOpen={isAddGoalModalOpen}
                onClose={closeAddGoalModal}
                enrollments={enrollments?.data || []}
            />
        </div>
    );
}