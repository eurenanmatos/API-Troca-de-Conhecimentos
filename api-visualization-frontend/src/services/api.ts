import axios from 'axios';

const API_BASE_URL = 'https://api.example.com'; // Replace with your API base URL

export const fetchApiData = async (endpoint: string) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/${endpoint}`);
        return response.data;
    } catch (error) {
        throw new Error(`Error fetching data from ${endpoint}: ${error}`);
    }
};