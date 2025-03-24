import { useState, FormEvent, useEffect } from "react";
import toast from "react-hot-toast";
import { useMe, useUpdateProfile } from "@/queries";

const ProfileUpdateForm = () => {
    const { data: user } = useMe();
    const { mutate: updateProfile, isPending: isUpdatingProfile, isError: isErrorUpdatingProfile, error: errorUpdatingProfile } = useUpdateProfile();

    const [isEditing, setIsEditing] = useState(false);
    const [username, setUsername] = useState(user?.data.username || "");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState(user?.data.firstName || "");
    const [lastName, setLastName] = useState(user?.data.lastName || "");
    const [email, setEmail] = useState(user?.data.email || "");
    const [avatarLink, setAvatarLink] = useState(user?.data.avatarLink || "");

    useEffect(() => {
        if (isErrorUpdatingProfile) {
            toast.error(errorUpdatingProfile?.response?.data?.message || "Error updating profile");
        }
    }, [isErrorUpdatingProfile, errorUpdatingProfile]);

    const handleUpdateProfile = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const profileData = { username, password, firstName, lastName, email, avatarLink };
        updateProfile(profileData);
        setIsEditing(false);
    };

    return (
        <div className="rounded-lg border p-4 shadow">
            <h2 className="mb-2 font-bold">Profile</h2>
            {isEditing ? (
                <form onSubmit={handleUpdateProfile} className="flex flex-col gap-2">
                    <label>
                        Username:
                        <input type="text" name="username" value={username} onChange={(e) => setUsername(e.target.value)} required className="border rounded p-2 w-full" />
                    </label>
                    <label>
                        Password:
                        <input type="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} className="border rounded p-2 w-full" />
                    </label>
                    <label>
                        First Name:
                        <input type="text" name="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} className="border rounded p-2 w-full" />
                    </label>
                    <label>
                        Last Name:
                        <input type="text" name="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} className="border rounded p-2 w-full" />
                    </label>
                    <label>
                        Email:
                        <input type="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)} className="border rounded p-2 w-full" />
                    </label>
                    <label>
                        Avatar Link:
                        <input type="text" name="avatarLink" value={avatarLink} onChange={(e) => setAvatarLink(e.target.value)} className="border rounded p-2 w-full" />
                    </label>
                    <div className="flex gap-2">
                        <button type="submit" disabled={isUpdatingProfile} className="mt-2 rounded bg-blue-500 px-4 py-2 text-white">
                            {isUpdatingProfile ? "Updating..." : "Update Profile"}
                        </button>
                        <button type="button" onClick={() => setIsEditing(false)} className="mt-2 rounded bg-gray-500 px-4 py-2 text-white">
                            Cancel
                        </button>
                    </div>
                </form>
            ) : (
                <div className="flex flex-col gap-2">
                    <div>Id: {user?.data.id}</div>
                    <div>Username: {user?.data.username}</div>
                    <div>First Name: {user?.data.firstName}</div>
                    <div>Last Name: {user?.data.lastName}</div>
                    <div>Email: {user?.data.email}</div>
                    <div>Avatar Link: {user?.data.avatarLink}</div>
                    <button onClick={() => setIsEditing(true)} className="mt-2 rounded bg-blue-500 px-4 py-2 text-white">
                        Edit Profile
                    </button>
                </div>
            )}
            {isErrorUpdatingProfile && <p className="text-red-500">{errorUpdatingProfile?.response?.data?.message || "Error updating profile"}</p>}
        </div>
    );
};

export default ProfileUpdateForm;