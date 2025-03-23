"use client";

import Navbar from "@/component/Navbar";
import { useLogout } from "@/queries/auth/useLogout";
import { useMe } from "@/queries/auth/useMe";
import { useRouter } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
import { useState, useEffect } from "react";

export default function NavbarLayout({ children }: Readonly<{ children: React.ReactNode }>) {
    const router = useRouter();
    const { mutate: logout } = useLogout();
    const { data: user } = useMe();
    const queryClient = useQueryClient();
    const [userData, setUserData] = useState(user);

    useEffect(() => {
        setUserData(user);
    }, [user]);

    const logoutOnClick = async () => {
        logout();
        setUserData(undefined);
        await queryClient.invalidateQueries();
        router.push("/");
    };

    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar user={userData?.data} logoutOnClick={logoutOnClick} />
            </div>
            {children}
        </div>
    );
}