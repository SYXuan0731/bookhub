const headers = ['productId', 'title', 'author', 'feedback', 'publishDate', 'category', 'description', "image"];
let role = "";

function handleUserRole() {
    const url = window.location.href;
    if (url.toLowerCase().includes("customer")) {
        role = "customer";
    } else {
        role = "admin";
    }
    if (role === "customer") {
        const addLink = document.getElementById("add-products")
        addLink ? addLink.style.display = "none" : null;
    }
}

async function deleteProduct(productId) {
    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            window.alert("Deleted Successfully");
            await fetchProducts();
        } else {
            const errorData = await response.json();
            window.alert('Delete Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error deleting product:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function fetchFeedbackCount(productId) {
    try {
        const response = await fetch(`/feedback/count/${productId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            return data;
        } else {
            console.error('Failed to fetch feedback count');
            return 0;
        }
    } catch (error) {
        console.error('Error fetching feedback count:', error);
        return 0;
    }
}

async function populateData(headers, data) {
    const table = document.getElementById("table");
    table.innerHTML = "";
    if (data.length === 0) {
        const noData = document.createElement("p");
        noData.innerHTML = "No Data Found";
        table.append(noData);
    }

    const thead = document.createElement("thead");
    const tr = document.createElement("tr");
    headers.forEach(header => {
        const th = document.createElement("th");
        th.innerHTML = header;
        tr.appendChild(th);
    });
    thead.appendChild(tr);
    table.appendChild(thead);

    const tbody = document.createElement("tbody");
    for (const product of data) {
        const tr = document.createElement("tr");
        for (const header of headers) {
            const td = document.createElement("td");
            if (header === "image") {
                const img = document.createElement("img");
                img.src = product[header];
                img.width = 50;
                td.appendChild(img);
            } else if (header === "feedback") {
                const feedbackCount = await fetchFeedbackCount(product.productId);
                const feedbackLink = document.createElement("a");
                const feedbackPage = role === "customer" ? "/Customer/product-feedback.html" : "/Admin/product-feedback.html";
                feedbackLink.href = `${feedbackPage}?id=${product.productId}`;
                feedbackLink.innerHTML = `Feedback (${feedbackCount})`;
                td.appendChild(feedbackLink);
            } else {
                td.innerHTML = product[header];
            }
            tr.appendChild(td);
        }

        // View button
        const viewTd = document.createElement("td");
        const viewLink = document.createElement("a");
        viewLink.href = `/Admin/product-view.html?id=${product.productId}`;
        viewLink.innerHTML = "view";
        viewLink.classList.add("btn", "btn-info");
        viewTd.appendChild(viewLink);
        tr.appendChild(viewTd);

        if (role === "admin") {
            // Edit button
            const editTd = document.createElement("td");
            const editLink = document.createElement("a");
            editLink.href = `/Admin/product-edit.html?id=${product.productId}`;
            editLink.innerHTML = "edit";
            editLink.classList.add("btn", "btn-warning");
            editTd.appendChild(editLink);
            tr.appendChild(editTd);

            // Delete button
            const deleteTd = document.createElement("td");
            const deleteButton = document.createElement("button");
            deleteButton.onclick = () => deleteProduct(product.productId);
            deleteButton.innerHTML = "delete";
            deleteButton.classList.add("btn", "btn-danger");
            deleteTd.appendChild(deleteButton);
            tr.appendChild(deleteTd);
        }

        tbody.appendChild(tr);
    }
    table.appendChild(tbody);
}

async function fetchProducts() {
    try {
        const response = await fetch('/products', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const responseData = await response.json();
            console.log({ responseData });
            products = Object.freeze(responseData);
            await filterProducts();
        } else {
            const errorData = await response.json();
            window.alert('Fetch Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error during fetch products:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function filterProducts() {
    const searchInput = document.getElementById("search").value;
    const categorySelect = document.getElementById("category").value;

    console.log(searchInput, categorySelect, products);
    if (searchInput === "" && categorySelect === "") {
        return await populateData(headers, products);
    }
    const filteredProducts = products.filter(product => {
        const title = product.title;
        const author = product.author;
        return (title.includes(searchInput) || author.includes(searchInput)) && product.category.includes(categorySelect);
    });
    console.log({ filteredProducts });
    await populateData(headers, filteredProducts);
}

async function formDeserialize(form, data) {
    for (const [key, val] of (new URLSearchParams(data)).entries()) {
        const input = form.elements[key];
        if (input.type === 'checkbox') {
            input.checked = !!val;
        } else {
            input.value = val;
        }
    }
}

async function populateFormData(data) {
    const form = document.getElementById("edit-form");
    const formData = new FormData();

    formData.append("title", data.title);
    formData.append("author", data.author);
    formData.append("publishDate", data.publishDate);
    formData.append("category", data.category);
    formData.append("description", data.description);
    // formData.append("image", `/images/${data.image}`);

    console.log({ formData });
    await formDeserialize(form, formData);
}

async function loadProductDetails() {
    const params = new URLSearchParams(window.location.search);
    productId = params.get('id');
    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const responseData = await response.json();
            await populateFormData(responseData);
        } else {
            const errorData = await response.json();
            window.alert('Fetch Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error during login:', error);
        window.alert('An error occurred. Please try again.');
    }
}


async function submitFormEdit(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData();
    const file = form.image.files[0];
    formData.append('image', file);
    formData.append("title", form.title.value);
    formData.append("author", form.author.value);
    formData.append("publishDate", form.publishDate.value);
    formData.append("category", form.category.value);
    formData.append("description", form.description.value);

    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'PATCH',
            body: formData,
        });

        if (response.ok) {
            const responseData = await response.json();
            window.alert(responseData.message);
            window.location.assign('product-index.html');
        } else {
            const errorData = await response.json();
            window.alert('Create Product failed: ' + errorData.message); // Show an alert with the error message
        }
    } catch (error) {
        console.error('Error during product creation:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function submitFormCreate(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData();
    const file = form.image.files[0];
    formData.append('image', file);
    formData.append("title", form.title.value);
    formData.append("author", form.author.value);
    formData.append("publishDate", form.publishDate.value);
    formData.append("category", form.category.value);
    formData.append("description", form.description.value);

    try {
        const response = await fetch("/product", {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            const responseData = await response.json();
            window.alert(responseData.message);
            window.location.assign('product-index.html');
        } else {
            const errorData = await response.json();
            window.alert('Create Product failed: ' + errorData.message); // Show an alert with the error message
        }
    } catch (error) {
        console.error('Error during product creation:', error);
        window.alert('An error occurred. Please try again.');
    }
}

// Initialize user role and fetch products on page load
window.onload = function() {
    handleUserRole();
    fetchProducts();
};
