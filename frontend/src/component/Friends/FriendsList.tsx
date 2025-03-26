import { useState } from 'react';
import { usePersons } from '@/queries/person/usePersons';
import { useFriends } from '@/queries/friend/useFriends';
import { useAddFriend, useRemoveFriend } from '@/queries/friend/useFriendMutations';
import { useMe } from '@/queries/auth/useMe';
import { useQueryClient } from '@tanstack/react-query';
import Image from 'next/image';
import toast from 'react-hot-toast';

export const FriendsList = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [activeTab, setActiveTab] = useState<'all' | 'friends'>('all');
    
    const queryClient = useQueryClient();
    const { data: personsData, isLoading: isLoadingPersons, error: personsError } = usePersons();
    const { data: friendsData, isLoading: isLoadingFriends, error: friendsError } = useFriends();
    const { data: meData } = useMe();
    
    const addFriendMutation = useAddFriend();
    const removeFriendMutation = useRemoveFriend();

    const isLoading = isLoadingPersons || isLoadingFriends;
    const error = personsError || friendsError;

    const handleAddFriend = (personId: number) => {
        addFriendMutation.mutate(
            { personId },
            {
                onSuccess: () => {
                    toast.success('Friend added successfully');
                    queryClient.invalidateQueries({ queryKey: ['friends'] });
                },
                onError: (error) => {
                    toast.error(error.response?.data.message || 'Failed to add friend');
                }
            }
        );
    };

    const handleRemoveFriend = (personId: number) => {
        removeFriendMutation.mutate(
            { personId },
            {
                onSuccess: () => {
                    toast.success('Friend removed successfully');
                    queryClient.invalidateQueries({ queryKey: ['friends'] });
                },
                onError: (error) => {
                    toast.error(error.response?.data.message || 'Failed to remove friend');
                }
            }
        );
    };

    if (isLoading) {
        return (
            <div className="w-full rounded-lg border shadow p-4">
                <div className="animate-pulse space-y-4">
                    <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                    <div className="h-10 bg-gray-200 rounded"></div>
                    <div className="space-y-3">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="flex items-center space-x-4">
                                <div className="rounded-full bg-gray-200 h-10 w-10"></div>
                                <div className="flex-1 space-y-2">
                                    <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                                    <div className="h-3 bg-gray-200 rounded w-1/5"></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="w-full rounded-lg border shadow p-4">
                <div className="text-red-500">Error loading users. Please try again later.</div>
            </div>
        );
    }

    // Create a set of friends' IDs for easy lookup
    const friendIds = new Set(friendsData?.data.map(friend => {
        // Find the person object that matches this friend's username
        const personMatch = personsData?.data.find(p => p.username === friend.username);
        return personMatch?.id;
    }).filter(Boolean));

    const currentUserId = meData?.data.id;

    // Filter persons based on active tab and search term
    const filteredPersons = personsData?.data
        // Filter out yourself
        .filter(person => person.id !== currentUserId)
        // Filter based on the active tab - either all users or just friends
        .filter(person => activeTab === 'all' || friendIds.has(person.id))
        // Filter based on search term
        .filter(person =>
            person.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (person.firstName && person.firstName.toLowerCase().includes(searchTerm.toLowerCase())) ||
            (person.lastName && person.lastName.toLowerCase().includes(searchTerm.toLowerCase()))
        ) || [];

    return (
        <div className="w-full rounded-lg border shadow p-4">
            <div className="pb-4">
                <h2 className="text-xl font-semibold mb-1">Users</h2>
                <p className="text-gray-600 text-sm mb-4">Find and connect with other learners</p>
                
                {/* Tab navigation */}
                <div className="flex mb-4 border-b">
                    <button
                        className={`px-4 py-2 font-medium ${activeTab === 'all' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'}`}
                        onClick={() => setActiveTab('all')}
                    >
                        All Users
                    </button>
                    <button
                        className={`px-4 py-2 font-medium ${activeTab === 'friends' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'}`}
                        onClick={() => setActiveTab('friends')}
                    >
                        My Friends {friendIds.size > 0 && `(${friendIds.size})`}
                    </button>
                </div>
                
                <div className="relative mb-4">
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder={`Search ${activeTab === 'friends' ? 'friends' : 'users'}...`}
                        className="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                {/* User/Friend List */}
                <div className="space-y-3 mt-4">
                    {filteredPersons.length === 0 ? (
                        <div className="text-center py-4 text-gray-500">
                            {searchTerm 
                                ? `No ${activeTab === 'friends' ? 'friends' : 'users'} found matching your search` 
                                : activeTab === 'friends' 
                                    ? "You don't have any friends yet" 
                                    : "No users available"}
                        </div>
                    ) : (
                        filteredPersons.map((person) => {
                            const isFriend = friendIds.has(person.id);
                            
                            return (
                                <div 
                                    key={person.id}
                                    className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg border"
                                >
                                    <div className="flex items-center space-x-4">
                                        <div className="relative w-10 h-10 rounded-full overflow-hidden bg-gray-200">
                                            {person.avatarLink ? (
                                                <Image
                                                    src={person.avatarLink}
                                                    alt={person.username}
                                                    fill
                                                    className="object-cover"
                                                />
                                            ) : (
                                                <div className="w-full h-full flex items-center justify-center text-gray-500">
                                                    {person.firstName?.[0] || person.username[0].toUpperCase()}
                                                </div>
                                            )}
                                        </div>
                                        <div>
                                            <div className="font-medium">{person.username}</div>
                                            {(person.firstName || person.lastName) && (
                                                <div className="text-sm text-gray-500">
                                                    {[person.firstName, person.lastName].filter(Boolean).join(' ')}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                    
                                    {isFriend ? (
                                        <button
                                            onClick={() => handleRemoveFriend(person.id)}
                                            disabled={removeFriendMutation.isPending}
                                            className="px-3 py-1 text-sm text-red-600 border border-red-600 rounded-md hover:bg-red-50"
                                        >
                                            {removeFriendMutation.isPending ? 'Removing...' : 'Remove Friend'}
                                        </button>
                                    ) : (
                                        <button
                                            onClick={() => handleAddFriend(person.id)}
                                            disabled={addFriendMutation.isPending}
                                            className="px-3 py-1 text-sm text-blue-600 border border-blue-600 rounded-md hover:bg-blue-50"
                                        >
                                            {addFriendMutation.isPending ? 'Adding...' : 'Add Friend'}
                                        </button>
                                    )}
                                </div>
                            );
                        })
                    )}
                </div>
            </div>
        </div>
    );
};