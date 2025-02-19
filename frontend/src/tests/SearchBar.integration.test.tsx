import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SearchBar from "../components/Searchbar";
import "@testing-library/jest-dom";

const mockCourses = [
  {
    id: 1,
    title: "Project Management Fundamentals",
    description: "Learn to manage projects effectively",
    link: "http://example.com/project",
  },
  {
    id: 2,
    title: "Introduction to AI",
    description: "Learn the basics of AI",
    link: "http://example.com/ai",
  },
];

beforeEach(() => {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve(mockCourses),
    })
  ) as jest.Mock;
});

afterEach(() => {
  jest.restoreAllMocks();
});

describe("SearchBar Component", () => {
  test("fetches courses and displays results", async () => {
    render(<SearchBar />);

    await waitFor(() => expect(global.fetch).toHaveBeenCalled());


    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    await waitFor(() => {
      expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument();
    });
  });

  test("filters search results correctly when typing", async () => {
    render(<SearchBar />);

    await waitFor(() => expect(global.fetch).toHaveBeenCalled());

    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    await waitFor(() => expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument());
  });

  test("navigates to the correct course page when a search result is clicked", async () => {
    render(<SearchBar />);

    await waitFor(() => expect(global.fetch).toHaveBeenCalled());

    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    await waitFor(() => expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument());

    const courseLink = screen.getByText("Project Management Fundamentals");
    fireEvent.click(courseLink);
  });
});
