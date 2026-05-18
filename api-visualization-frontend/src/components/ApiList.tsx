import React from 'react';

interface ApiListProps {
    apiData: { id: number; name: string; description: string }[];
}

const ApiList: React.FC<ApiListProps> = ({ apiData }) => {
    return (
        <div>
            <h2>API List</h2>
            <ul>
                {apiData.map(api => (
                    <li key={api.id}>
                        <h3>{api.name}</h3>
                        <p>{api.description}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ApiList;