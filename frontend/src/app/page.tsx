import Navbar from "@/components/Navbar";
import CourseList from "../components/CourseList";

export default function HomePage() {
    return (
        <div className="flex">
            <div className="m-4 grow">
                <Navbar />
                <CourseList />
            </div>
        </div>
    );
}
